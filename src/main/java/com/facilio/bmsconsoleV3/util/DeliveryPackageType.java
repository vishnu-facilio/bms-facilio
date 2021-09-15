package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import software.amazon.awssdk.services.textract.model.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DeliveryPackageType {


    UPS(1) {
        @Override
        protected boolean detect(List<Block>  textBlocks) throws Exception {
            for(Block block : textBlocks){
                if (block.text() != null && block.text().toLowerCase().contains("ups")) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getTrackingNumber() throws Exception {
            Pattern pattern = Pattern.compile("TRACKING\\s#:\\s(.*)");
            for(Block block : getOcrTextBlocks()){
                if(block.text() != null){
                    Matcher matcher = pattern.matcher(block.text());
                    if(matcher.matches()){
                        return block.text().substring(12, matcher.end());
                    }
                }
            }
            return null;
        }

        @Override
        public EmployeeContext getEmployee() throws Exception {
            List<Block> textBlocks = new ArrayList<>();
            Pattern pattern = Pattern.compile("^SHIP TO:$");
            int counter = 0;
            boolean is_name_found = false;
            for (Block block :  getOcrTextBlocks()) {
                if(block.text() != null){
                    Matcher matcher = pattern.matcher(block.text());
                    if(matcher.matches()){
                        is_name_found = true;
                    }
                }
                if(is_name_found && counter < 8) {
                    textBlocks.add(block);
                    counter++;
                } else if(counter == 8){
                    break;
                }
            }
            return textBlocks.size() == 0? findEmployee(getOcrTextBlocks()): findEmployee(textBlocks);
        }
    },

    FEDEX(2) {
        @Override
        protected boolean detect(List<Block>  textBlocks) throws Exception {
            for(Block block : textBlocks){
                if (block.text() != null && block.text().toLowerCase().contains("fedex")) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getTrackingNumber() throws Exception {
            Pattern pattern = Pattern.compile("\\d{4} \\d{4} \\d{4}");
            for(Block block : getOcrTextBlocks()){
                if(block.text() != null){
                    Matcher matcher = pattern.matcher(block.text());
                    if(matcher.matches()){
                        return block.text().substring(matcher.start(), matcher.end());
                    }
                }
            }
            return null;
        }

        @Override
        public EmployeeContext getEmployee() throws Exception {
            return null;
        }
    },

    USPS(3) {
        @Override
        protected boolean detect(List<Block>  textBlocks) throws Exception {
            for(Block block : textBlocks){
                if (block.text() != null && block.text().toLowerCase().contains("usps")) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getTrackingNumber() throws Exception {
            Pattern pattern = Pattern.compile("\\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{2}");
            for(Block block : getOcrTextBlocks()){
                if(block.text() != null){
                    Matcher matcher = pattern.matcher(block.text());
                    if(matcher.matches()){
                        return block.text().substring(matcher.start(), matcher.end());
                    }
                }
            }
            return null;
        }

        @Override
        public EmployeeContext getEmployee() throws Exception {
            List<Block> textBlocks = new ArrayList<>();
            Pattern pattern = Pattern.compile("^SHIP$");
            int counter = 0;
            boolean is_name_found = false;
            for (Block block :  getOcrTextBlocks()) {
                if(block.text() != null){
                    Matcher matcher = pattern.matcher(block.text());
                    if(matcher.matches()){
                        is_name_found = true;
                    }
                }
                if(is_name_found && counter < 8) {
                    textBlocks.add(block);
                    counter++;
                } else if(counter == 8){
                    break;
                }
            }
            return textBlocks.size() == 0? findEmployee(getOcrTextBlocks()): findEmployee(textBlocks);
        }
    },

    AMAZON(4) {
        @Override
        protected boolean detect(List<Block>  textBlocks) throws Exception {
            for(Block block : textBlocks){
                if (block.text() != null && block.text().toLowerCase().contains("amazon")) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getTrackingNumber() throws Exception {
            Pattern pattern = Pattern.compile("#(.*)#");
            for(Block block : getOcrTextBlocks()){
                if(block.text() != null){
                    Matcher matcher = pattern.matcher(block.text());
                    if(matcher.matches()){
                        return block.text().substring(matcher.start() + 1, matcher.end() - 1);
                    }
                }
            }
            return null;
        }

        @Override
        public EmployeeContext getEmployee() throws Exception {
            return findEmployee(getOcrTextBlocks());
        }
    },

    DHL(5) {
        @Override
        protected boolean detect(List<Block>  textBlocks) throws Exception {
            for(Block block : textBlocks){
                if (block.text() != null && block.text().toLowerCase().contains("wpx")) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getTrackingNumber() throws Exception {
            Pattern pattern1 = Pattern.compile("\\d{2} \\d{4} \\d{4}");
            Pattern pattern2 = Pattern.compile("\\w{2,3}\\d{10,11}");
            for(Block block : getOcrTextBlocks()){
                if(block.text() != null){
                    Matcher matcher1 = pattern1.matcher(block.text());
                    Matcher matcher2 = pattern2.matcher(block.text());
                    if(matcher1.matches()){
                        return block.text().substring(matcher1.start(), matcher1.end());
                    }
                    if(matcher2.matches()){
                        return block.text().substring(matcher2.start(), matcher2.end());
                    }
                }
            }
            return null;
        }

        @Override
        public EmployeeContext getEmployee() throws Exception {
            List<Block> textBlocks = new ArrayList<>();
            Pattern pattern = Pattern.compile("^To$");
            int counter = 0;
            boolean is_name_found = false;
            for (Block block :  getOcrTextBlocks()) {
                if(block.text() != null){
                    Matcher matcher = pattern.matcher(block.text());
                    if(matcher.matches()){
                        is_name_found = true;
                    }
                }
                if(is_name_found && counter < 8) {
                    textBlocks.add(block);
                    counter++;
                } else if(counter == 8){
                    break;
                }
            }
            return findEmployee(textBlocks);
        }
    },

    DEUTSCHE_POST(6) {
        @Override
        protected boolean detect(List<Block>  textBlocks) throws Exception {
            for(Block block : textBlocks){
                if (block.text() != null && block.text().toLowerCase().contains("deutsche post")) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getTrackingNumber() throws Exception {
            Pattern pattern = Pattern.compile("\\d{12}");
            for(Block block : getOcrTextBlocks()){
                if(block.text() != null){
                    Matcher matcher = pattern.matcher(block.text());
                    if(matcher.matches()){
                        return block.text().substring(matcher.start(), matcher.end());
                    }
                }
            }
            return null;
        }

        @Override
        public EmployeeContext getEmployee() throws Exception {
            List<Block> textBlocks = new ArrayList<>();
            Pattern pattern = Pattern.compile("^To$");
            int counter = 0;
            boolean is_name_found = false;
            for (Block block :  getOcrTextBlocks()) {
                if(block.text() != null){
                    Matcher matcher = pattern.matcher(block.text());
                    if(matcher.matches()){
                        is_name_found = true;
                    }
                }
                if(is_name_found && counter < 8) {
                    textBlocks.add(block);
                    counter++;
                } else if(counter == 8){
                    break;
                }
            }
            return textBlocks.size() == 0? findEmployee(getOcrTextBlocks()): findEmployee(textBlocks);
        }
    },

    ROYAL_MAIL(7) {
        @Override
        protected boolean detect(List<Block>  textBlocks) throws Exception {
            for(Block block : textBlocks){
                if (block.text() != null && block.text().toLowerCase().contains("royal post")) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getTrackingNumber() throws Exception {
            Pattern pattern = Pattern.compile("#(.*)#");
            for(Block block : getOcrTextBlocks()){
                if(block.text() != null){
                    Matcher matcher = pattern.matcher(block.text());
                    if(matcher.matches()){
                        return block.text().substring(matcher.start() + 1, matcher.end() - 1);
                    }
                }
            }
            return null;
        }

        @Override
        public EmployeeContext getEmployee() throws Exception {
            return findEmployee(getOcrTextBlocks());
        }
    },
    OTHER(8){
        @Override
        protected boolean detect(List<Block> textBlocks) throws Exception {
            return true;
        }

        @Override
        public String getTrackingNumber() throws Exception {
            return null;
        }

        @Override
        public EmployeeContext getEmployee() throws Exception {
            return findEmployee(getOcrTextBlocks());
        }
    }
    ;

    private int enumValue;

    public List<Block> getOcrTextBlocks() {
        return ocrTextBlocks;
    }

    public void setOcrTextBlocks(List<Block> ocrTextBlocks) {
        this.ocrTextBlocks = ocrTextBlocks;
    }

    private List<Block> ocrTextBlocks;

    private DeliveryPackageType(int enumValue) {
        this.enumValue = enumValue;
    }

    public int getCarrierType() {
        return this.enumValue;
    }

    protected abstract boolean detect(List<Block> textBlocks) throws Exception;
    public abstract String getTrackingNumber() throws Exception;
    public abstract EmployeeContext getEmployee() throws Exception;

    public static EmployeeContext findEmployee(List<Block> textBlocks) throws Exception {
        String commaSeparatedNames = "";
        for(Block block : textBlocks){
            if(block.text() != null){
                commaSeparatedNames += block.text() + ",";
            }
        }
        String modName = "Employee";
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(modName);
        List<FacilioField> fields = modBean.getAllFields(modName);

        SelectRecordsBuilder<EmployeeContext> select = new SelectRecordsBuilder<EmployeeContext>().module(module)
                                                                .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                                                                .select(fields)
                                                                .andCondition(CriteriaAPI.getCondition("NAME", "name", commaSeparatedNames, StringOperators.IS));
        List<EmployeeContext> result =  select.get();
        if(result.size() != 0) {

            return (result.size() == 1) ? result.get(0) : result.get(result.size() - 1);
        }
        return null;
    }

    public static DeliveryPackageType detectPackageType(List<Block>  textBlocks) throws Exception {
        for (DeliveryPackageType packageType : values()) {
            if (packageType.detect(textBlocks)) {
                packageType.setOcrTextBlocks(textBlocks);
                return packageType;
            }
        }
        return null;
    }
}