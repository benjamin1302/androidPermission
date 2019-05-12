package com;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class main {

    private static List<File> files = new ArrayList<>();

    //////////////////////////////// ATTENTION À BIEN CHANGER LE PATH /////////////////////////////
    private static String CSV_MAPPING_511 = "/Users/keeedl/COURS/BIBLIO/GIT/mapping_4.1.1.csv";
    
    private static String ANDROID_MANIFEST_PATH_IN_PROJECT = null;
    private static String PATH_FILES = null;

    public static void main(String args[]) {

        for (int i=1; i<8; i++) {

            //////////////////////////////// ATTENTION À BIEN CHANGER LES PATHS /////////////////////////////
            PATH_FILES = "/Users/keeedl/COURS/BIBLIO/apk-malware/apk-malware-" + i + "/sources/com";
            ANDROID_MANIFEST_PATH_IN_PROJECT = "/Users/keeedl/COURS/BIBLIO/apk-malware/apk-malware-" + i +"/AndroidManifest.xml";
            ////////////////////////////////////////////////////////////////////////////////////////////////

            final File folder = new File(PATH_FILES);

            System.out.println("Reading malware " + i);
            List<String> getPermissionsFromManifest = getPermissionsAndroidManifest();
            HashMap<String, HashSet<String>> permissionsAndFunctions = getPermissionsAndFunctionList();

            HashSet<String> usedPermissions = new HashSet<>();

            for (String permissionManifest : getPermissionsFromManifest) {
                if (permissionsAndFunctions.containsKey(permissionManifest)) {

                    HashSet<String> functionsMatchingWithPermission = permissionsAndFunctions.get(permissionManifest);

                    functionsMatchingWithPermission.forEach(functionMatchingWithPermission -> {
                        AtomicBoolean isUsedPermissionInSourceFiles = searchWordInFiles((listFilesFromFolder(folder)), functionMatchingWithPermission, permissionManifest);
                        if (isUsedPermissionInSourceFiles.get()) {
                            usedPermissions.add(permissionManifest);
                        }
                    });

                }
            }

            createFileTxt("malware" + i, getPermissionsFromManifest, usedPermissions);

        }

    }

    private static List<String> getDangerousPermission(List<String> getPermissionsFromManifest) {

        List<String> dangerousPermissions = Arrays.asList("READ_CALENDAR","WRITE_CALENDAR","CAMERA","READ_CONTACTS","WRITE_CONTACTS","GET_ACCOUNTS","ACCESS_FINE_LOCATION","ACCESS_COARSE_LOCATION","RECORD_AUDIO","READ_PHONE_STATE","READ_PHONE_NUMBERS","CALL_PHONE","ANSWER_PHONE_CALLS","READ_CALL_LOG","WRITE_CALL_LOG","ADD_VOICEMAIL","USE_SIP","PROCESS_OUTGOING_CALLS","BODY_SENSORS","SEND_SMS","RECEIVE_SMS","READ_SMS","RECEIVE_WAP_PUSH","RECEIVE_MMS","READ_EXTERNAL_STORAGE","WRITE_EXTERNAL_STORAGE");

        List<String> dangerousPermissionRequested = new ArrayList<>();

        for(String permissionManifest : getPermissionsFromManifest) {
            if(dangerousPermissions.contains(permissionManifest)) {
                dangerousPermissionRequested.add(permissionManifest);
            }
        }
        return dangerousPermissionRequested;
    }

    private static void createFileTxt(String fileName, List<String> getPermissionsFromManifest, HashSet<String> usedPermissions) {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(fileName + ".txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        writer.println("Application : " + fileName);
        writer.println();

        writer.println("Permissions de l'AndroidManifest.xml :");
        for(String permissionManifest : getPermissionsFromManifest) {
            writer.println(permissionManifest);
        }
        writer.println();

        writer.println("Permissions utilisées dans le code :");
        for(String usedPermission : usedPermissions) {
            writer.println(usedPermission);
        }
        writer.println();

        int cpt = 0;
        writer.println("Permissions non utilisées dans le code :");
        for(String permisionManifest : getPermissionsFromManifest) {
            if(!usedPermissions.contains(permisionManifest)) {
                writer.println(permisionManifest);
                cpt++;
            }
        }
        writer.println();

        /*List<String> dangerousPermissions = getDangerousPermission(getPermissionsFromManifest);
        writer.println("Permissions demandées dangereuses :");
        for(String dangerousPermission : dangerousPermissions) {
                writer.println(dangerousPermission);
            }
        writer.println();

        writer.println("Permissions non utilisées et dangereuses :");
        int cptDeux = 0;
        for(String permisionManifest : getPermissionsFromManifest) {
            if(!usedPermissions.contains(permisionManifest) && dangerousPermissions.contains(permisionManifest)) {
                writer.println(permisionManifest);
                cptDeux++;
            }
        }
        writer.println();*/

        writer.println("Nombre de permissions du manifest : " + getPermissionsFromManifest.size());
        writer.println("Nombre de permissions du manifest utilisées : : " + usedPermissions.size());
        writer.println("Nombre de permissions du manifest non utilisées : " + cpt);
        //writer.println("Nombre de permissions dangereuses : " + dangerousPermissions.size());
        //writer.println("Nombre de permissions non utilisées et dangereuses : " + cptDeux);

        writer.close();
    }

    private static HashMap<String, HashSet<String>> getPermissionsAndFunctionList() {


        HashMap<String, HashSet<String>> permissions = new HashMap<>();
        List<String> records = new ArrayList<>();
        HashSet<String> test = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(CSV_MAPPING_511))) {
            while (scanner.hasNextLine()) {
                String permissionName = null;
                records.add(scanner.nextLine());
                if(records.get(records.size()-1).contains("android.permission.")) {
                    int tailleMot = new String("android.permission.").length();
                    int position = records.get(records.size()-1).lastIndexOf("android.permission.")+tailleMot;
                    int positionVirgule = records.get(records.size()-1).lastIndexOf(",");
                    permissionName = records.get(records.size()-1).substring(position, positionVirgule);
                    if(!permissions.containsKey(permissionName)) {
                        test = new HashSet<>();
                    }
                }

                int positionVirgule = records.get(records.size()-1).indexOf(",");
                int positionParenthese = records.get(records.size()-1).indexOf("(");

                if(positionParenthese != -1) {
                    String functionCall = records.get(records.size()-1).substring(positionVirgule, positionParenthese).replace(",", "");

                    if(!functionCall.contains("<init>")) {
                        test.add(functionCall);
                        permissions.put(permissionName, test);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return permissions;
    }


    private static AtomicBoolean searchWordInFiles(List<File> javaFiles, String searchingWord, String permission) {

        AtomicBoolean isUsedPermission = new AtomicBoolean(false);

        javaFiles.forEach(javaFile -> {
            for(String data : getDatasFromFile(javaFile.getPath())) {

                if(data.contains(searchingWord)) {
                    int position = data.lastIndexOf(searchingWord);
                    System.out.println("Fonction utilisée : " + data.substring(position) + "-- Permission : " + permission);
                    System.out.println(javaFile.getPath());
                    isUsedPermission.set(true);
                    break;
                }
                if(isUsedPermission.get()) {
                    break;
                }
            }
        });

        return isUsedPermission;
    }

    private static List<String> getPermissionsAndroidManifest() {

        List<String> isAskedPermissions = new ArrayList<>();
        for(String data : getDatasFromFile(ANDROID_MANIFEST_PATH_IN_PROJECT)) {

            if(data.contains("android:name=\"android.permission.")) {
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
            } else {
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
        while (sc.hasNext()) {
            accounts.add((sc.next()));
        }

        return accounts;
    }
}
