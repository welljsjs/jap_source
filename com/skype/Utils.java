/*
 * Decompiled with CFR 0.150.
 */
package com.skype;

import com.skype.CommandFailedException;
import com.skype.NotAttachedException;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.SkypeExceptionHandler;
import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.TimeOutException;

final class Utils {
    static String convertNullToEmptyString(String string) {
        if (string == null) {
            return "";
        }
        return string;
    }

    static void convertToSkypeException(ConnectorException connectorException) throws SkypeException {
        SkypeException skypeException = connectorException instanceof com.skype.connector.NotAttachedException ? new NotAttachedException() : (connectorException instanceof TimeOutException ? new com.skype.TimeOutException(connectorException.getMessage()) : new SkypeException(connectorException.getMessage()));
        skypeException.initCause(connectorException);
        throw skypeException;
    }

    static void checkError(String string) throws SkypeException {
        if (string == null) {
            return;
        }
        if (string.startsWith("ERROR ")) {
            throw new CommandFailedException(string);
        }
    }

    static String getPropertyWithCommandId(String string, String string2, String string3) throws SkypeException {
        try {
            String string4 = "GET " + string + " " + string2 + " " + string3;
            String string5 = string + " " + string2 + " " + string3 + " ";
            String string6 = Connector.getInstance().executeWithId(string4, string5);
            Utils.checkError(string6);
            return string6.substring(string5.length());
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
            return null;
        }
    }

    static String getProperty(String string, String string2, String string3) throws SkypeException {
        try {
            String string4 = "GET " + string + " " + string2 + " " + string3;
            String string5 = string + " " + string2 + " " + string3 + " ";
            String string6 = Connector.getInstance().execute(string4, string5);
            Utils.checkError(string6);
            return string6.substring(string5.length());
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
            return null;
        }
    }

    static String getProperty(String string, String string2) throws SkypeException {
        try {
            String string3 = "GET " + string + " " + string2;
            String string4 = string + " " + string2 + " ";
            String string5 = Connector.getInstance().execute(string3, string4);
            Utils.checkError(string5);
            return string5.substring(string4.length());
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
            return null;
        }
    }

    static String getProperty(String string) throws SkypeException {
        try {
            String string2 = "GET " + string + " ";
            String string3 = string + " ";
            String string4 = Connector.getInstance().execute(string2, string3);
            Utils.checkError(string4);
            return string4.substring(string3.length());
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
            return null;
        }
    }

    static void setProperty(String string, String string2, String string3, String string4) throws SkypeException {
        try {
            String string5 = "SET " + string + " " + string2 + " " + string3 + " " + string4;
            String string6 = string + " " + string2 + " " + string3 + " " + string4;
            String string7 = Connector.getInstance().execute(string5, string6);
            Utils.checkError(string7);
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
        }
    }

    static void setProperty(String string, String string2, String string3) throws SkypeException {
        try {
            String string4 = "SET " + string + " " + string2 + " " + string3;
            String string5 = string + " " + string2 + " " + string3;
            String string6 = Connector.getInstance().execute(string4, string5);
            Utils.checkError(string6);
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
        }
    }

    static void setProperty(String string, String string2) throws SkypeException {
        try {
            String string3 = "SET " + string + " " + string2;
            String string4 = string + " " + string2;
            String string5 = Connector.getInstance().execute(string3, string4);
            Utils.checkError(string5);
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
        }
    }

    static void executeWithErrorCheck(String string) throws SkypeException {
        try {
            String string2 = Connector.getInstance().execute(string);
            Utils.checkError(string2);
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
        }
    }

    static void checkNotNull(String string, Object object) {
        if (object == null) {
            throw new NullPointerException("The " + string + " must not be null.");
        }
    }

    static String[] convertToArray(String string) {
        if ("".equals(string)) {
            return new String[0];
        }
        return string.split(", ");
    }

    static String convertToCommaSeparatedString(String[] arrstring) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrstring.length; ++i) {
            if (i != 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(arrstring[i]);
        }
        return stringBuffer.toString();
    }

    static void handleUncaughtException(Throwable throwable, SkypeExceptionHandler skypeExceptionHandler) {
        if (skypeExceptionHandler != null) {
            skypeExceptionHandler.uncaughtExceptionHappened(throwable);
            return;
        }
        Skype.handleUncaughtException(throwable);
    }

    private Utils() {
    }
}

