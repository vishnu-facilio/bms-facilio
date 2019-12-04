package com.facilio.bmsconsole.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class AgentDownloadAction extends ActionSupport {

    JSONObject result = new JSONObject();

    public FileInputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }
    //will be downloaded from the url in the param
    FileInputStream fileInputStream = new FileInputStream(new File("/home/anand/app.json"));

    public AgentDownloadAction() throws FileNotFoundException, ParseException {

    }

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }

    public String getTest() {
        return test;
    }


    public void setTest(String test) {
        this.test = test;
    }
    private JSONObject object = new JSONObject();
    private String test = "12345";

    public JSONObject getObject() {
        return object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }

    void ActionDownloadAction(){

    }
    public String downloadAgent(){

        return SUCCESS;
    }

}
