package com.facilio.ocr.aws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.EntityType;
import software.amazon.awssdk.services.textract.model.Relationship;
import software.amazon.awssdk.services.textract.model.RelationshipType;
import software.amazon.awssdk.services.textract.model.SelectionStatus;

public class TextractExtractor {
	
	static String getText(Block result, Map<String, Block> blockMap,boolean isKey) {
        StringBuilder text = new StringBuilder();
        if (result.relationships() != null) {
            for (Relationship relationship : result.relationships()) {
                if (relationship.type() == RelationshipType.CHILD) {
                    for (String childId : relationship.ids()) {
                        Block word = blockMap.get(childId);
                        if (word.blockType() == BlockType.WORD) {
                            text.append(word.text()).append(" ");
                        }
                        if (word.blockType() == BlockType.SELECTION_ELEMENT) {
                            if (word.selectionStatus() == SelectionStatus.SELECTED) {
                                text.append("X ");
                            }
                        }
                    }
                }
            }
        }
        String resultString = text.toString();
        resultString = resultString.trim();
        if(isKey) {
            if(resultString.endsWith(":")) {
                resultString = resultString.substring(0,resultString.length()-1);
            }
        }
        return resultString;
    }

	static class RawTextExtractor {
		public static String getRawText(List<Block> blocks) {
			StringBuilder rawTextBuilder = new StringBuilder();
			for(Block block : blocks) {
				if(block.blockType() == BlockType.LINE) {
					rawTextBuilder.append(block.text());
					rawTextBuilder.append("\n");
				}
			}
			return rawTextBuilder.toString();
		}
	}
	static class KeyValueExtractor {

	    public static List<TextractContext.FormContext> getKeyValues(List<Block> blocks) {
	    	
	    	Map<String, Block> keyMap = new HashMap<>();
	        
	        Map<String, Block> valueMap = new HashMap<>();
	        Map<String, Block> blockMap = new HashMap<>();
	        
	        
	        for (Block block : blocks) {
	            String blockId = block.id();
	            blockMap.put(blockId, block);
	            if (block.blockType() == BlockType.KEY_VALUE_SET) {
	                if (block.entityTypes().contains(EntityType.KEY)) {
	                    keyMap.put(blockId, block);
	                } else {
	                    valueMap.put(blockId, block);
	                }
	            }
	        }

			List<TextractContext.FormContext> kvs = getKeyValueRelationship(keyMap, valueMap, blockMap);
	        
	        return kvs;
	       
	    }
	    

	    private static List<TextractContext.FormContext> getKeyValueRelationship(Map<String, Block> keyMap, Map<String, Block> valueMap, Map<String, Block> blockMap) {
	    	Map<Integer,Map<String, String>> kvs = new HashMap<>();
			List<TextractContext.FormContext> formContexts = new ArrayList<>();
	        for (Map.Entry<String, Block> entry : keyMap.entrySet()) {
	            Block keyBlock = entry.getValue();

	            Integer page = keyBlock.page();
				List<TextractContext.FormContext> formContextsForCurrentPage = formContexts.stream().filter(k -> k.getPage() == page).collect(Collectors.toList());
				List<TextractContext.FormContext.FormValuesContext> formRuleContexts = new ArrayList<>();
				if(CollectionUtils.isNotEmpty(formContextsForCurrentPage)){
					formRuleContexts = formContextsForCurrentPage.get(0).getFormRules();
				}

	            Map<String, String> pageMap = kvs.computeIfAbsent(page, k-> new HashMap<String, String>());
	            String key = getText(keyBlock, blockMap,true);

	            if(key != null && !key.isEmpty()) {
	                Block valueBlock = findValueBlock(keyBlock, valueMap);
	                String value = getText(valueBlock, blockMap,false);

	                if(!pageMap.containsKey(key)) {
						TextractContext.FormContext.FormValuesContext formRuleContext = new TextractContext.FormContext.FormValuesContext();
						formRuleContext.setKey(key);
						formRuleContext.setValue(value);
						formRuleContext.setKeyGeomentry(keyBlock.geometry());
						formRuleContext.setValueGeometry(valueBlock.geometry());
						formRuleContext.setKeyId(keyBlock.id());
						formRuleContext.setValueId(valueBlock.id());
						formRuleContexts.add(formRuleContext);
						pageMap.put(key,value);
	                }
	            }

				if(CollectionUtils.isEmpty(formContextsForCurrentPage)) {
					TextractContext.FormContext formContext = new TextractContext.FormContext();
					formContext.setPage(page);
					formContext.setFormRules(formRuleContexts);
					formContexts.add(formContext);
				}
	        }
	        return formContexts;
	    }

	    private static Block findValueBlock(Block keyBlock, Map<String, Block> valueMap) {
	        for (Relationship relationship : keyBlock.relationships()) {
	            if (relationship.type() == RelationshipType.VALUE) {
	                for (String valueId : relationship.ids()) {
	                    return valueMap.get(valueId);
	                }
	            }
	        }
	        return null;
	    }

		public static Map<Integer,Map<String, String>> getKeyValuePairRelationshipAsMap(List<TextractContext.FormContext> keyValuePair) {
			Map<Integer,Map<String, String>> kvs = new HashMap<>();

			for(TextractContext.FormContext formContext: keyValuePair){
				int page = formContext.getPage();
				Map<String, String> pageMap = kvs.computeIfAbsent(page, k-> new HashMap<String, String>());

				for(TextractContext.FormContext.FormValuesContext formRuleContext: formContext.getFormRules()){
					if(!pageMap.containsKey(formRuleContext.getKey())){
						pageMap.put(formRuleContext.getKey(), formRuleContext.getValue());
					}
				}
			}

			return kvs;
		}
	}
	
	static class TableValueExtractor {

	    public static List<TextractContext.TableContext> getTableResults(List<Block> blocks) throws IOException {

	        Map<String, Block> blocksMap = new HashMap<>();
	        List<Block> tableBlocks = new ArrayList<>();

	        for (Block block : blocks) {
	            blocksMap.put(block.id(), block);
	            if (block.blockType() == BlockType.TABLE) {
	                tableBlocks.add(block);
	            }
	        }

			List<TextractContext.TableContext> tableContextList = new ArrayList<>();

	        if (!tableBlocks.isEmpty()) {
	            
	            for (int i = 0; i < tableBlocks.size(); i++) {
	            	
	                Block tableResult = tableBlocks.get(i);
	                
	                Map<String, Map<String, TextractContext.TableRecordContext>> rows = getRowsColumnsMap(tableResult, blocksMap);
	                String tableId = "Table_" + (i+1);

					TextractContext.TableContext tableContext = new TextractContext.TableContext();
					List<List<TextractContext.TableRecordContext>> tableList = new ArrayList<>();
	                for (Map.Entry<String, Map<String, TextractContext.TableRecordContext>> row : rows.entrySet()) {
						List<TextractContext.TableRecordContext> valueList = new ArrayList<>();
						tableList.add(valueList);
	                    for (Map.Entry<String, TextractContext.TableRecordContext> col : row.getValue().entrySet()) {
							valueList.add(col.getValue());
	                    }
	                }
					tableContext.setName(tableId);
					tableContext.setRecords(tableList);
					tableContextList.add(tableContext);
	            }
	        }
	        return tableContextList;
	    }

		public static Map<String,Map<Integer,Map<Integer,String>>> getTableResultAsMap(List<TextractContext.TableContext> tableList) throws IOException {
			Map<String,Map<Integer,Map<Integer,String>>> result = new HashMap<>();
			for(TextractContext.TableContext tableContext: tableList){
				Map<Integer,Map<Integer,String>> tableMap = result.computeIfAbsent(tableContext.getName(), k-> new HashMap<Integer,Map<Integer,String>>());
				List<List<TextractContext.TableRecordContext>> records = tableContext.getRecords();
				int rowCount = 1;
				for(List<TextractContext.TableRecordContext> rows: records){
					Map<Integer,String> valueMap = tableMap.computeIfAbsent(rowCount++, k-> new HashMap<Integer,String>());
					int columnCount = 1;
					for(TextractContext.TableRecordContext col: rows){
						valueMap.put(columnCount++,col.getName());
					}
				}
			}

			return result;
		}


		private static Map<String, Map<String, TextractContext.TableRecordContext>> getRowsColumnsMap(Block tableResult, Map<String, Block> blocksMap) {
	        Map<String, Map<String, TextractContext.TableRecordContext>> rows = new HashMap<>();
	        List<String> scores = new ArrayList<>();

	        for (Relationship relationship : tableResult.relationships()) {
	            if (relationship.type() == RelationshipType.CHILD) {
	                for (String childId : relationship.ids()) {
	                    Block cell = blocksMap.get(childId);
	                    if (cell.blockType() == BlockType.CELL) {
	                        String rowIndex = cell.rowIndex().toString();
	                        String colIndex = cell.columnIndex().toString();
	                        rows.computeIfAbsent(rowIndex, k -> new HashMap<>());
	                        scores.add(cell.confidence().toString());

							TextractContext.TableRecordContext tableRecordContext = new TextractContext.TableRecordContext();
							tableRecordContext.setName(getText(cell, blocksMap,true));
							tableRecordContext.setGeomentry(cell.geometry());
							tableRecordContext.setId(cell.id());
	                        rows.get(rowIndex).put(colIndex, tableRecordContext);
	                    }
	                }
	            }
	        }

	        return rows;
	    }

	}

}
