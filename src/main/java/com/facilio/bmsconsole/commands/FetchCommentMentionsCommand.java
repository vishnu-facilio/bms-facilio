package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.CommentMentionContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.*;

public class FetchCommentMentionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
            List<Long> mentionedPeopleIds = new ArrayList<>();
            if (notes != null && notes.size() > 0) {
                for (NoteContext note : notes) {
                    List<CommentMentionContext> mentions = NotesAPI.getNoteMentions(note.getId(), moduleName);
                    note.setMentions(mentions);
                    ArrayList<Long> pplIdsFromMention = getPeopleIdsFromMentions(mentions);
                    if(pplIdsFromMention != null && !pplIdsFromMention.isEmpty()){
                        mentionedPeopleIds.addAll(pplIdsFromMention);
                    }
                }
                Map<Long, V3PeopleContext> peopleRecords = getPeopleRecords(mentionedPeopleIds);
                setPeopleForMentions(notes,peopleRecords);
            }
        return false;
    }

    private void setPeopleForMentions(List<NoteContext> notes, Map<Long, V3PeopleContext> peopleRecords) {
        for(NoteContext note:notes){
            List<CommentMentionContext> mentions = note.getMentions();
            if(mentions == null || mentions.isEmpty()){
                continue;
            }
            for (CommentMentionContext mention: mentions) {
                if(mention.getMentionTypeEnum() == CommentMentionContext.MentionType.PEOPLE && mention.getMentionedRecordId() > 0){
                    V3PeopleContext ppl = peopleRecords.get(mention.getMentionedRecordId());
                    if(ppl == null){
                        continue;
                    }
                    mention.setRecordObj(ppl);
                }
            }
        }
    }

    private static Map<Long, V3PeopleContext> getPeopleRecords(List<Long> mentionedPeopleIds) throws Exception {
        Map<Long,V3PeopleContext> pplWithId = new HashMap<>();
        if(mentionedPeopleIds != null || mentionedPeopleIds.isEmpty()){
            Set<Long> pplSet = new HashSet<>(mentionedPeopleIds);
            List<V3PeopleContext> mentionedPeopleList = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.PEOPLE, pplSet, V3PeopleContext.class);
            if(mentionedPeopleList != null || !mentionedPeopleList.isEmpty()){
                for (V3PeopleContext ppl : mentionedPeopleList) {
                    pplWithId.put(ppl.getId(), ppl);
                }
            }
        }
        return pplWithId;
    }

    private static ArrayList<Long> getPeopleIdsFromMentions(List<CommentMentionContext> mentions) throws Exception {
        ArrayList< Long> peopleIds = new ArrayList<>();
        if( mentions != null && !mentions.isEmpty()) {
            for(CommentMentionContext mentionContext : mentions){
                if(mentionContext.getMentionTypeEnum() == CommentMentionContext.MentionType.PEOPLE){
                    if(mentionContext.getMentionedRecordId() > 0){
                        peopleIds.add(mentionContext.getMentionedRecordId());
                    }
                }
            }
        }
        return peopleIds;
    }
}
