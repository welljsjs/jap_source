/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp;

import java.io.Serializable;
import java.util.Calendar;

public class FTPFile
implements Serializable {
    public static final int FILE_TYPE = 0;
    public static final int DIRECTORY_TYPE = 1;
    public static final int SYMBOLIC_LINK_TYPE = 2;
    public static final int UNKNOWN_TYPE = 3;
    public static final int USER_ACCESS = 0;
    public static final int GROUP_ACCESS = 1;
    public static final int WORLD_ACCESS = 2;
    public static final int READ_PERMISSION = 0;
    public static final int WRITE_PERMISSION = 1;
    public static final int EXECUTE_PERMISSION = 2;
    int _type = 3;
    int _hardLinkCount = 0;
    long _size = 0L;
    String _rawListing = null;
    String _user = null;
    String _group = null;
    String _name = null;
    String _link;
    Calendar _date = null;
    boolean[][] _permissions = new boolean[3][3];

    public void setRawListing(String string) {
        this._rawListing = string;
    }

    public String getRawListing() {
        return this._rawListing;
    }

    public boolean isDirectory() {
        return this._type == 1;
    }

    public boolean isFile() {
        return this._type == 0;
    }

    public boolean isSymbolicLink() {
        return this._type == 2;
    }

    public boolean isUnknown() {
        return this._type == 3;
    }

    public void setType(int n) {
        this._type = n;
    }

    public int getType() {
        return this._type;
    }

    public void setName(String string) {
        this._name = string;
    }

    public String getName() {
        return this._name;
    }

    public void setSize(long l) {
        this._size = l;
    }

    public long getSize() {
        return this._size;
    }

    public void setHardLinkCount(int n) {
        this._hardLinkCount = n;
    }

    public int getHardLinkCount() {
        return this._hardLinkCount;
    }

    public void setGroup(String string) {
        this._group = string;
    }

    public String getGroup() {
        return this._group;
    }

    public void setUser(String string) {
        this._user = string;
    }

    public String getUser() {
        return this._user;
    }

    public void setLink(String string) {
        this._link = string;
    }

    public String getLink() {
        return this._link;
    }

    public void setTimestamp(Calendar calendar) {
        this._date = calendar;
    }

    public Calendar getTimestamp() {
        return this._date;
    }

    public void setPermission(int n, int n2, boolean bl) {
        this._permissions[n][n2] = bl;
    }

    public boolean hasPermission(int n, int n2) {
        return this._permissions[n][n2];
    }

    public String toString() {
        return this._rawListing;
    }
}

