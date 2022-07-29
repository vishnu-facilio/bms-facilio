package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ColorPaletteAction extends FacilioAction {
    private static final long serialVersionUID = 1L;
    private ColorPaletteContext colorPalette ;
    public ColorPaletteContext getColorPalette() {
        return colorPalette;
    }
    public void setColorPalette(ColorPaletteContext colorPalette) {
        this.colorPalette = colorPalette;
    }
    @Getter @Setter
    private List<String> keys;
    @Getter @Setter
    private long id;


    public String addColorPalette () throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getAddColorPaletteChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ColourPalette.COLOR_PALETTE, colorPalette);
        chain.execute();
        setResult(FacilioConstants.ColourPalette.COLOR_PALETTE, context.get(FacilioConstants.ColourPalette.COLOR_PALETTE));
        return SUCCESS;
        }
    public String listColorPalette () throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getListColorPaletteChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ColourPalette.COLOR_KEYS, getKeys());
        chain.execute();
        setResult(FacilioConstants.ColourPalette.COLOR_PALETTE, context.get(FacilioConstants.ColourPalette.COLOR_PALETTE));
        return SUCCESS;
        }
    public String deleteColorPalette () throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getDeleteColorPaletteChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        chain.execute();
        setResult(FacilioConstants.ColourPalette.COLOR_PALETTE, "ColorPalette Deleted Successfully");
        return SUCCESS;
        }

}
