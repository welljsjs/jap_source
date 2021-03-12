/*
 * Decompiled with CFR 0.150.
 */
package com.skype;

import com.skype.Friend;
import com.skype.SkypeException;
import com.skype.SkypeObject;
import com.skype.Utils;
import java.util.Hashtable;

public class User
extends SkypeObject {
    private static final Hashtable users = new Hashtable();
    public static final String STATUS_PROPERTY = "status";
    public static final String MOOD_TEXT_PROPERTY = "moodText";
    private String id;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static User getInstance(String string) {
        Hashtable hashtable = users;
        synchronized (hashtable) {
            if (!users.containsKey(string)) {
                users.put(string, new User(string));
            }
            return (User)users.get(string);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Friend getFriendInstance(String string) {
        Hashtable hashtable = users;
        synchronized (hashtable) {
            if (!users.containsKey(string)) {
                Friend friend = new Friend(string);
                users.put(string, friend);
                return friend;
            }
            User user = (User)users.get(string);
            if (user instanceof Friend) {
                return (Friend)user;
            }
            Friend friend = new Friend(string);
            friend.copyFrom(user);
            users.put(string, friend);
            return friend;
        }
    }

    User(String string) {
        this.id = string;
    }

    public final int hashCode() {
        return this.getId().hashCode();
    }

    public final boolean equals(Object object) {
        if (object instanceof User) {
            User user = (User)object;
            return this.getId().equals(user.getId());
        }
        return false;
    }

    public final String toString() {
        return this.getId();
    }

    public final String getId() {
        return this.id;
    }

    public final String getFullName() throws SkypeException {
        return this.getProperty("FULLNAME");
    }

    public final String getLauguage() throws SkypeException {
        return this.getLanguage();
    }

    public final String getLanguage() throws SkypeException {
        String string = this.getProperty("LANGUAGE");
        if ("".equals(string)) {
            return "";
        }
        return string.substring(string.indexOf(32) + 1);
    }

    public final String getLanguageByISOCode() throws SkypeException {
        String string = this.getProperty("LANGUAGE");
        if ("".equals(string)) {
            return "";
        }
        return string.substring(0, string.indexOf(32));
    }

    public final String getCountry() throws SkypeException {
        String string = this.getProperty("COUNTRY");
        if ("".equals(string)) {
            return "";
        }
        return string.substring(string.indexOf(32) + 1);
    }

    public final String getCountryByISOCode() throws SkypeException {
        String string = this.getProperty("COUNTRY");
        if ("".equals(string)) {
            return "";
        }
        return string.substring(0, string.indexOf(32));
    }

    public final String getProvince() throws SkypeException {
        return this.getProperty("PROVINCE");
    }

    public final String getCity() throws SkypeException {
        return this.getProperty("CITY");
    }

    public final String getHomePhone() throws SkypeException {
        return this.getHomePhoneNumber();
    }

    public final String getHomePhoneNumber() throws SkypeException {
        return this.getProperty("PHONE_HOME");
    }

    public final String getOfficePhone() throws SkypeException {
        return this.getOfficePhoneNumber();
    }

    public final String getOfficePhoneNumber() throws SkypeException {
        return this.getProperty("PHONE_OFFICE");
    }

    public final String getMobilePhone() throws SkypeException {
        return this.getMobilePhoneNumber();
    }

    public final String getMobilePhoneNumber() throws SkypeException {
        return this.getProperty("PHONE_MOBILE");
    }

    public final String getHomePageAddress() throws SkypeException {
        return this.getProperty("HOMEPAGE");
    }

    public final String getAbout() throws SkypeException {
        return this.getIntroduction();
    }

    public final String getIntroduction() throws SkypeException {
        return this.getProperty("ABOUT");
    }

    public String getMoodMessage() throws SkypeException {
        return this.getProperty("MOOD_TEXT");
    }

    public String getSpeedDial() throws SkypeException {
        return this.getProperty("SPEEDDIAL");
    }

    public void getSpeedDial(String string) throws SkypeException {
        this.setProperty("SPEEDDIAL", string);
    }

    public int getTimeZone() throws SkypeException {
        return Integer.parseInt(this.getProperty("TIMEZONE"));
    }

    public final String getDisplayName() throws SkypeException {
        return this.getProperty("DISPLAYNAME");
    }

    public final boolean isVideoCapable() throws SkypeException {
        return Boolean.parseBoolean(this.getProperty("IS_VIDEO_CAPABLE"));
    }

    public final boolean isAuthorized() throws SkypeException {
        return Boolean.parseBoolean(this.getProperty("ISAUTHORIZED"));
    }

    public final void setAuthorized(boolean bl) throws SkypeException {
        this.setProperty("ISAUTHORIZED", bl);
    }

    public final boolean isBlocked() throws SkypeException {
        return Boolean.parseBoolean(this.getProperty("ISBLOCKED"));
    }

    public final boolean canLeaveVoiceMail() throws SkypeException {
        return Boolean.parseBoolean(this.getProperty("CAN_LEAVE_VM"));
    }

    public final boolean isForwardingCalls() throws SkypeException {
        return Boolean.parseBoolean(this.getProperty("IS_CF_ACTIVE"));
    }

    public final void setBlocked(boolean bl) throws SkypeException {
        this.setProperty("ISBLOCKED", bl);
    }

    private String getProperty(String string) throws SkypeException {
        return Utils.getProperty("USER", this.getId(), string);
    }

    private void setProperty(String string, boolean bl) throws SkypeException {
        this.setProperty(string, ("" + bl).toUpperCase());
    }

    private void setProperty(String string, String string2) throws SkypeException {
        Utils.setProperty("USER", this.getId(), string, string2);
    }

    public final void setDisplayName(String string) throws SkypeException {
        Utils.setProperty("USER", this.getId(), "DISPLAYNAME", string);
    }

    final void dispose() {
        users.remove(this.getId());
    }
}

