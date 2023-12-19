package com.facilio.pdftemplate.util;

import com.github.jknack.handlebars.Handlebars;

public class HandleBarsHelper {

    private Handlebars handlebars;

    public HandleBarsHelper(){
        this.handlebars = new Handlebars();
        registerCustomHelpers();
    }

    public Handlebars getHandlebars() {
        return handlebars;
    }

    private void registerCustomHelpers(){
        registerGroupDataHelper();
    }
    private void registerGroupDataHelper(){

    }
}
