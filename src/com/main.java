package com;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class main {

    private static List<File> files = new ArrayList<>();
    private static final String APPLICATION_PACKAGE_PATH = "/Users/keeedl/COURS/BIBLIO/Castle_Clash-apk-files/test";
    private static final String CSV_MAPPING_511 = "/Users/keeedl/COURS/BIBLIO/GIT/mapping_5.1.1.csv";
    private static final String ANDROID_MANIFEST_PATH_IN_PROJECT = "AndroidManifest.xml";

    public static void main(String args[]) {

       final File folder = new File(APPLICATION_PACKAGE_PATH);
        List<Permission> permissionsAndFunctions = getPermissionsAndFunctionList();
        List<String> getPermissionsFromManifest = getPermissionsAndroidManifest();
        List<String> match = new ArrayList<>();

        permissionsAndFunctions.forEach(permissionAndFuntion -> {
            for (String permissionManifest : getPermissionsFromManifest) {
                if(permissionManifest.equals(permissionAndFuntion.getPermissionName())) {

                    List<String> getPermissionsFromApiCall = searchWordInFiles((listFilesFromFolder(folder)), permissionAndFuntion.getFunctionName());
                    //
                    if(!match.contains(permissionManifest)) {
                        match.add(permissionManifest);
                    }
                    System.out.println("Match des permissions : " + permissionManifest);
                }
            }
        });
        match.forEach(matche -> System.out.println(matche));

        //getPermissionsFromManifest.forEach(permission -> System.out.println(permission));

        //List<String> getPermissionsFromApiCall = searchWordInFiles((listFilesFromFolder(folder)));

        //List<String> realUsedPermissions = checkUsedAndCalledPermissions(getPermissionsFromManifest, getPermissionsFromApiCall);

        //displayUsedAndUnusedPermissions(getPermissionsFromManifest, realUsedPermissions);
    }

    private static List<Permission> getPermissionsAndFunctionList() {


        List<Permission> permissions = new ArrayList<>();
        List<String> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(CSV_MAPPING_511))) {
            while (scanner.hasNextLine()) {
                Permission permission = new Permission();
                records.add(scanner.nextLine());
                if(records.get(records.size()-1).contains("android.permission.")) {
                    int tailleMot = new String("android.permission.").length();
                    int position = records.get(records.size()-1).lastIndexOf("android.permission.")+tailleMot;
                    int positionVirgule = records.get(records.size()-1).lastIndexOf(",");
                    String permissionName = records.get(records.size()-1).substring(position, positionVirgule);
                    permission.setPermissionName(permissionName);

                }

                int positionVirgule = records.get(records.size()-1).indexOf(",");
                int positionParenthese = records.get(records.size()-1).indexOf("(");

                if(positionParenthese != -1) {
                    String functionCall = records.get(records.size()-1).substring(positionVirgule, positionParenthese).replace(",", "");
                    permission.setFunctionName(functionCall);
                }
                permissions.add(permission);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        permissions.forEach(permission -> {
            System.out.println(permission.getPermissionName() + "  -  " + permission.getFunctionName());
        });
        System.out.println("nombre de fonctions : " + permissions.size());

        return permissions;
    }

    private static void displayUsedAndUnusedPermissions(List<String> getPermissionsFromManifest, List<String> realUsedPermissions) {

        realUsedPermissions.forEach(realUsedPermission -> {
            if(getPermissionsFromManifest.contains(realUsedPermission)) {
                System.out.println("com.Permission demandée et utilisée : " + realUsedPermission);
            } else {
                System.out.println("com.Permission demandée et non-utilisée : " + realUsedPermission);
            }
        });
    }

    private static List<String> checkUsedAndCalledPermissions(List<String> getPermissionsFromManifest, List<String> getPermissionsFromApiCall) {

        List<String> usedPermissions = new ArrayList<>();
        getPermissionsFromApiCall.forEach(
                checkedPermission -> {
                    for(String askedPermission : getPermissionsFromManifest) {
                        if(checkedPermission.contains(askedPermission)) {
                            //System.out.println("com.Permission demandée et utilisée : " + askedPermission);
                            if(!usedPermissions.contains(askedPermission)) {
                                usedPermissions.add(askedPermission);
                            }
                        }
                    }
                }
        );

        return usedPermissions;
    }

    private static List<String> searchWordInFiles(List<File> javaFiles, String searchingWord) {

        List<String> isUsedToCheckPermission = new ArrayList<>();
        javaFiles.forEach(javaFile -> {
            for(String data : getDatasFromFile(javaFile.getPath())) {

                if(data.contains(searchingWord)) {
                    int position = data.lastIndexOf(searchingWord);
                    isUsedToCheckPermission.add(data.substring(position));
                    System.out.println("Fonction utilisée : " + data.substring(position));
                    System.out.println(javaFile.getPath());
                }
            }
        });
        return isUsedToCheckPermission;
    }

    private static List<String> getPermissionsAndroidManifest() {

        List<String> isAskedPermissions = new ArrayList<>();
        for(String data : getDatasFromFile(ANDROID_MANIFEST_PATH_IN_PROJECT)) {

            if(data.contains("android.permission.")) {
                int position = data.lastIndexOf("\"/>");
                System.out.println("Permission demandée : " + data.substring(33, position));
                isAskedPermissions.add(data.substring(33, position));
            }
        }
        return isAskedPermissions;
    }

    private static List<File> listFilesFromFolder(final File folder) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                listFilesFromFolder(fileEntry);
                //files.add(fileEntry);
            } else {
                //System.out.println(fileEntry.getName());
                files.add(fileEntry);
            }
        }
        return files;
    }

    private static List<String> getDatasFromFile(String fileName) {

        List<String> accounts = new ArrayList<>();

        Scanner sc = null;
        try {
            sc = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("error file : file not found ");
            e.printStackTrace();
        }
        //while (sc.hasNextLine()) {
        while (sc.hasNext()) {
            //System.out.println(sc.next());
            //accounts.add((sc.nextLine()));
            accounts.add((sc.next()));
        }

        return accounts;
    }
}
