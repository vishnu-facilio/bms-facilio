package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PageSectionContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PageSectionsAction extends FacilioAction{
    private long id;
    private long previousId;
    private long nextId;
    private PageSectionContext section;
    private long columnId;
    private long sectionId;

    public String addPageSection() throws Exception{
        FacilioChain chain = TransactionChainFactory.getAddPageSectionChain();
        FacilioContext context = chain.getContext();
        if(columnId<=0){
            throw new IllegalArgumentException("Invalid column id for creating section");
        }
        Map<Long, List<PageSectionContext>>  columnSectionsMap = new HashMap<>();
        columnSectionsMap.put(columnId, new ArrayList<>(Arrays.asList(section)));
        context.put(FacilioConstants.CustomPage.COLUMN_SECTIONS_MAP, columnSectionsMap);
        chain.execute();
        setResult(FacilioConstants.CustomPage.SECTION_ID, columnSectionsMap.get(columnId).get(0).getId());
        return SUCCESS;
    }

    public String getPageSection() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getPageSectionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        chain.execute();
        section = (PageSectionContext) context.get(FacilioConstants.CustomPage.SECTION);
        setResult(FacilioConstants.CustomPage.SECTION,section);
        return SUCCESS;
    }

    public String patchPageSection() throws Exception{
        FacilioChain chain = TransactionChainFactory.getPatchPageSectionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.SECTION, section);
        chain.execute();
        return SUCCESS;
    }

    public String deletePageSection() throws Exception{
        FacilioChain chain = TransactionChainFactory.getDeletePageComponentChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.MODULE, ModuleFactory.getPageSectionsModule());
        chain.execute();
        return SUCCESS;
    }

    public String reorderPageSections() throws Exception{
        FacilioChain chain = TransactionChainFactory.getReorderPageSectionsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.PREVIOUS_ID,previousId);
        context.put(FacilioConstants.ContextNames.ID,id);
        context.put(FacilioConstants.CustomPage.NEXT_ID,nextId);
        context.put(FacilioConstants.CustomPage.COLUMN_ID,columnId);
        context.put(FacilioConstants.CustomPage.TYPE, CustomPageAPI.PageComponent.SECTION);
        chain.execute();
        double sequenceNumber = (double) context.get(FacilioConstants.CustomPage.SEQUENCE_NUMBER);
        setResult(FacilioConstants.CustomPage.SEQUENCE_NUMBER, sequenceNumber);
        return SUCCESS;
    }
}
