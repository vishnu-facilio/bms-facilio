package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.modules.FacilioEnum;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

public class AddSignupDataCommandV3 extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(AddSignupDataCommandV3.class.getName());
    private static final String DEFAULT_SIGNUP_CONF_PATH = "conf/signup.yml";
    private static final List<SignUpData> SIGN_UP_CHAIN = initSignUpChain();
    private static List<SignUpData> initSignUpChain() {
        Yaml yaml = new Yaml();
        Map<String, Object> json = null;
        try(InputStream inputStream = FacilioEnum.class.getClassLoader().getResourceAsStream(DEFAULT_SIGNUP_CONF_PATH);) {
            json = yaml.load(inputStream);
        }
        catch (Exception e) {
            throwRunTimeException(MessageFormat.format("Error occurred while reading signup conf file, msg : {0}",e.getMessage()), e);
        }

        try {
            List<String> classes = (List<String>) json.get("classes");
            if (CollectionUtils.isNotEmpty(classes)) {
                List<SignUpData> signupChain = new ArrayList<>();
                for (String className : classes) {
                    try {
                        signupChain.add((SignUpData) Class.forName(className).newInstance());
                    }
                    catch (Exception e) {
                        throwRunTimeException(MessageFormat.format("Error occurred while creating instance of sign up class {0}, msg : {1}", className, e.getMessage()), e);
                    }
                }
                return Collections.unmodifiableList(signupChain);
            }
            else {
                return Collections.emptyList();
            }
        }
        catch (Exception e) {
            throwRunTimeException(MessageFormat.format("Error occurred while parsing signup conf file, msg : {0}",e.getMessage()), e);
        }
        return null;
    }
    public static void initSignUpDataClasses() {
        LOGGER.info(MessageFormat.format("No. of signup data classes : {0}", SIGN_UP_CHAIN.size()));
    }

    private static void throwRunTimeException (String logMsg, Throwable e) {
        LOGGER.error(logMsg, e);
        throw new RuntimeException(logMsg, e);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(CollectionUtils.isNotEmpty(SIGN_UP_CHAIN)){
            for(SignUpData signUpData : SIGN_UP_CHAIN){
                signUpData.addData();
            }
        }
        return false;
    }
}
