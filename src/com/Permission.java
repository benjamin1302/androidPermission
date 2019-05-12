package com;

import java.util.HashMap;
import java.util.List;

public class Permission {

    private HashMap<String, List<String>> hashMap = new HashMap<>();
    //private String permissionName;
    //private String functionName;

    public Permission() {
    }

    public void setHashMap(HashMap<String, List<String>> hashMap) {
        this.hashMap = hashMap;
    }

    public HashMap<String, List<String>> getHashMap() {
        return hashMap;
    }

    /*   public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }*/
}