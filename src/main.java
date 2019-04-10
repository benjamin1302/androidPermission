import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class main {

    private static List<File> files = new ArrayList<>();

    public static void main(String args[]) {

       //getPermissionsAndroidManifest();

        final File folder = new File("/Users/keeedl/COURS/BIBLIO/Castle_Clash-apk-files/sources/com");
        List<String> getPermissionsFromManifest = getPermissionsAndroidManifest();
        List<String> getPermissionsFromApiCall = getCallAPI((listFilesFromFolder(folder)));

        List<String> realUsedPermissions = checkUsedAndCalledPermissions(getPermissionsFromManifest, getPermissionsFromApiCall);

        displayUsedAndUnusedPermissions(getPermissionsFromManifest, realUsedPermissions);
    }

    private static void displayUsedAndUnusedPermissions(List<String> getPermissionsFromManifest, List<String> realUsedPermissions) {

        realUsedPermissions.forEach(realUsedPermission -> {
            if(getPermissionsFromManifest.contains(realUsedPermission)) {
                System.out.println("Permission demandée et utilisée : " + realUsedPermission);
            } else {
                System.out.println("Permission demandée et non-utilisée : " + realUsedPermission);
            }
        });
    }

    private static List<String> checkUsedAndCalledPermissions(List<String> getPermissionsFromManifest, List<String> getPermissionsFromApiCall) {

        List<String> usedPermissions = new ArrayList<>();
        getPermissionsFromApiCall.forEach(
                checkedPermission -> {
                    for(String askedPermission : getPermissionsFromManifest) {
                        if(checkedPermission.contains(askedPermission)) {
                            //System.out.println("Permission demandée et utilisée : " + askedPermission);
                            if(!usedPermissions.contains(askedPermission)) {
                                usedPermissions.add(askedPermission);
                            }
                        }
                    }
                }
        );

        return usedPermissions;
    }

    private static List<String> getCallAPI(List<File> javaFiles) {

        List<String> isUsedToCheckPermission = new ArrayList<>();
        javaFiles.forEach(javaFile -> {
            for(String data : getDatasFromFile(javaFile.getPath())) {

                if(data.contains(".checkPermission")) {
                    int position = data.lastIndexOf(".checkPermission");
                    isUsedToCheckPermission.add(data.substring(position));
                    System.out.println("Permission appelée : " + data.substring(position));
                }
            }
        });
        return isUsedToCheckPermission;
    }

    private static List<String> getPermissionsAndroidManifest() {

        List<String> isAskedPermissions = new ArrayList<>();
        for(String data : getDatasFromFile("AndroidManifest.xml")) {
            //System.out.println(data);

            if(data.contains("android.permission.")) {
                int position = data.lastIndexOf("\"/>");
                System.out.println("Permission demandée : " + data.substring(33, position));
                isAskedPermissions.add(data.substring(33, position));
                // System.out.println(data);
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
