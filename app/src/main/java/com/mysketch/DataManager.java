package com.mysketch;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/***
 * This Class is used to control all elements of managing data and files for the App.
 *
 * Features;
 * - Create/load/delete/rename projects.
 * - Dynamically save/load shapes for a given project into files.
 * - Export to PDF file in common 'Downloads' folder. @TODO
 *
 *
 * Requirements:
 * - External Memory (SD-Card etc.)
 * - AndroidPermissions: 'WRITE_EXTERNAL_STORAGE' and 'READ_EXTERNAL_STORAGE'
 * - Shapes Class having public fields 'projectName' and 'uniqueID'
 * - Activity Class keeping track of which project it is working in
 * - Activity Class calling the appropriate methods
 *
 *
 * By:
 * - Michael Davidsen Kirkegaard
 *
 */

public class DataManager {

    private static final String LOGTAG = "DataManager";

    static String DOWNLIOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    static String DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MySketch/Saves/";
    static final String FILE_TYPE = ".shape";

    //Used when choosing with project to open / delete
    public static ArrayList<String> getAllNamesOfProjects(){
        File[] allFiles = new File(DIR).listFiles();
        if(allFiles != null && allFiles.length > 0) {
            ArrayList<String> allProjects = new ArrayList<>();
            for (File file : allFiles) {
                if (file.isDirectory()) {
                    allProjects.add(file.getName());
                }
            }
            return allProjects;
        }
        Log.w(LOGTAG, "No Projects in directory/folder \""+DIR+"\"");
        return null;
    }

    //Creates folder for a new Project
    public static boolean newProject(String projectName){
        File projectDir = new File(DIR, projectName);
        if(!projectDir.exists()){
            if(projectDir.mkdirs()){
                Log.i(LOGTAG, "Project \""+projectDir.getAbsolutePath()+"/\" created");
                return true;
            }
            else{
                Log.e(LOGTAG, "Can't create project folder \""+projectDir.getAbsolutePath()+"/\"");
            }
        }
        else{
            Log.e(LOGTAG, "Project \""+projectDir.getAbsolutePath()+"/\" already exists");
        }
        return false;
    }

    //Deletes a folder and all its content
    public static boolean deleteProject(String projectName){
        File projectDir = new File(DIR, projectName);
        try {
            FileUtils.deleteDirectory(projectDir);
            Log.i(LOGTAG, "Project folder \""+projectDir.getAbsolutePath()+"\" deleted");
            return true;
        } catch (IOException e) {
            Log.e(LOGTAG, "Can't delete project folder \""+projectDir.getAbsolutePath()+"/\"");
            e.printStackTrace();
            return false;
        }
    }

    //Creates new folder directory, loads and deletes all files in old directory,
    //updates loaded shapes and finally saves all files in new directory.
    public static boolean renameProject(String oldProjectName, String newProjectName){
        //Creates new directory if possible
        if(!newProject(newProjectName)){
            return false;
        }

        //loads and deletes all old files
        ShapeWrapper[] allShapes = loadAllShapes(oldProjectName, true);
        if(allShapes == null){
            return false;
        }

        //changes the project field of loaded files
        //also updates UniqueIDs for recycling purposes
        for(int i = 0; i < allShapes.length; i++){
            allShapes[i].projectName = newProjectName;
            allShapes[i].uniqueID = i;
        }

        //saves all files in new directory
        if(!saveAndOverwriteAllShapes(allShapes)){
            return false;
        }

        //deletes directory and all other files in the old directory.
        if(!deleteProject(oldProjectName)){
            return false;
        }
        //success!
        Log.i(LOGTAG, "Project \""+DIR+oldProjectName+"/\" successfully moved to \""+DIR+newProjectName+"/\"");
        return true;
    }

    //Saves multiple new files or updates (overwriting) existing files
    public static boolean saveAndOverwriteAllShapes(Shapes[] sources){
        ShapeWrapper[] wrappers = new ShapeWrapper[sources.length];
        for(int i = 0; i < sources.length; i++){
            wrappers[i] = new ShapeWrapper().setup(sources[i]);
        }
        return saveAndOverwriteAllShapes(wrappers);
    }
    private static boolean saveAndOverwriteAllShapes(ShapeWrapper[] sources){
        boolean[] checkArray = new boolean[sources.length];
        for(int i = 0; i < sources.length; i++) {
            checkArray[i] = saveAndOverwriteSingleShape(sources[i]);
            if (!checkArray[i]) {
                Log.e(LOGTAG, "Error saving all files in project \"" + DIR + sources[i].projectName + "/" + sources[i] + "\"");
                return false;
            }
        }
        return true;
    }

    //Saves a single Shapes type object
    public static boolean saveAndOverwriteSingleShape(Shapes source) {
        return saveAndOverwriteSingleShape(new ShapeWrapper().setup(source));
    }
    private static boolean saveAndOverwriteSingleShape(ShapeWrapper source) {
        File projectDir = new File(DIR, source.projectName);

        try {
            File file = new File(projectDir, source.uniqueID+FILE_TYPE);
            if(file.exists()){
                if(!file.delete()){
                    Log.e(LOGTAG, "Error deleting old file: \""+DIR+source.projectName+"/"+source.uniqueID+FILE_TYPE+"\"");
                    return false;
                }
            }
            if(!file.createNewFile()){
                Log.e(LOGTAG, "Error creating new file: \""+DIR+source.projectName+"/"+source.uniqueID+FILE_TYPE+"\"");
                return false;
            }

            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            Log.i(LOGTAG, source.projectName+" - "+source.uniqueID);

            oos.writeObject(source);

            oos.flush();
            oos.close();

            return true;
        }
        catch(IOException e) {
            Log.e(LOGTAG, e.getMessage() + " : Error saving file: \""+DIR+source.projectName+"/"+source.uniqueID+FILE_TYPE+"\"");
            e.printStackTrace();
        }
        return false;
    }

    //Loads all shapes in projects directory, and boolean decides to delete or not.
    public static Shapes[] loadAllShapes(Context context, String projectName, boolean delete){
        ShapeWrapper[] wrappers = loadAllShapes(projectName, delete);
        if(wrappers != null && wrappers.length > 0){
            Shapes[] shapes = new Shapes[wrappers.length];
            for(int i = 0; i < wrappers.length; i++){
                shapes[i] = wrappers[i].convert(context);

                //Updates uniqueID for recycling
                shapes[i].uniqueID = i;
            }
            return shapes;
        }
        return null;
    }
    private static ShapeWrapper[] loadAllShapes(String projectName, boolean delete) {
        File projectDir = new File(DIR, projectName);

        File[] savedFiles = getCorrectFileTypes(projectDir);
        if(savedFiles == null){
            return null;
        }

        ShapeWrapper[] returnArray = new ShapeWrapper[savedFiles.length];
        for(int i = 0; i < savedFiles.length; i++){
            returnArray[i] = loadSingleFile(savedFiles[i], delete);
            if(returnArray[i] == null){
                Log.e(LOGTAG, "Error loading file at index: '"+i+"' in project \""+projectDir.getAbsolutePath()+"\"");
                return null;
            }
        }

        Log.i(LOGTAG, "All files successfully loaded from project \""+projectDir.getAbsolutePath()+"\"");
        return returnArray;
    }

    //loads a single file
    public static Shapes loadSingleFile(Context context, String projectName, int uniqueID, boolean delete){
        return loadSingleFile(new File(DIR+projectName, uniqueID + FILE_TYPE), delete).convert(context);
    }
    private static ShapeWrapper loadSingleFile(File file, boolean delete){
        if(!file.exists()){
            Log.e(LOGTAG, "No file found at \""+file.getAbsolutePath()+"\"");
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            ShapeWrapper returnObject = (ShapeWrapper) ois.readObject();

            if(returnObject == null){
                Log.e(LOGTAG, "Null object loaded from file\""+file.getAbsolutePath()+"\"");
                return null;
            }

            if(delete){
                if(file.delete()){
                    Log.e(LOGTAG, "Can't delete file \""+file.getAbsolutePath()+"\"");
                    return null;
                }
            }
            return returnObject;
        }
        catch(IOException | ClassNotFoundException e) {
            Log.e(LOGTAG, e.getMessage() + " : Error loading file: \""+file.getAbsolutePath()+"\"");
            e.printStackTrace();
        }
        return null;
    }

    //when object is deleted, the corresponding file should also be deleted.
    @SuppressWarnings("unused")
    public static boolean deleteSingleShape(Shapes source){
        if(new File(DIR+source.projectName, source.uniqueID+FILE_TYPE).delete()){
            Log.i(LOGTAG, "Deleting file: "+DIR+source.projectName+"/"+source.uniqueID+FILE_TYPE);
            return true;
        }
        else{
            Log.e(LOGTAG, "Can't delete file \""+DIR+source.projectName+"/"+source.uniqueID+FILE_TYPE+"\"");
            return false;
        }
    }

    //gets all files in directory that are shapes
    private static File[] getCorrectFileTypes(File projectDir) {
        File[] allFiles = projectDir.listFiles();

        if(allFiles != null && allFiles.length > 0) {
            ArrayList<File> savedFiles = new ArrayList<>();
            for (File file : allFiles) {
                if (file.getAbsolutePath().endsWith(FILE_TYPE)) {
                    savedFiles.add(file);
                }
            }
            return savedFiles.toArray(new File[savedFiles.size()]);
        }

        Log.w(LOGTAG, "No files in directory/folder \""+projectDir.getAbsolutePath()+"/\"");
        return null;
    }

    //makes the uniqueIDs 'sort-of' recycle
    public static int getUniqueID(String projectName){
        File projectDir = new File(DIR, projectName);
        File[] savedFiles = getCorrectFileTypes(projectDir);

        int i = 0;
        if(savedFiles != null){
            while(i < savedFiles.length){
                if(!savedFiles[i].exists()){
                    break;
                }
                i++;
            }
        }
        Log.i(LOGTAG, "UniqueID \""+i+"\" given in project \""+projectDir.getAbsolutePath()+"\"");
        return i;
    }

    private static class ShapeWrapper implements Serializable {
        //Common values
        String projectName;
        int uniqueID;
        String shapeType;
        float x;
        float y;
        float strokeWidth;

        //Circle
        float radius;

        //Square
        float width;
        float height;

        //Line
        float x2;
        float y2;

        ShapeWrapper(){}

        ShapeWrapper setup(Shapes source){
            projectName = source.projectName;
            shapeType = source.shapeType;
            uniqueID = source.uniqueID;
            x = source.getX();
            y = source.getY();
            strokeWidth = source.getStrokeWidth();

            switch (shapeType){
                case Circle.SHAPE_TYPE:{
                    radius = ((Circle) source).radius;
                    break;
                }
                case Square.SHAPE_TYPE:{
                    width = ((Square) source).width;
                    height = ((Square) source).height;
                    break;
                }
                case Line.SHAPE_TYPE:{
                    x2 = ((Line) source).x2;
                    y2 = ((Line) source).y2;
                    break;
                }
            }

            return this;
        }

        Shapes convert(Context context){
            Shapes output;
            switch(this.shapeType){
                case Circle.SHAPE_TYPE:{
                    output = new Circle(context, this.projectName, this.x, this.y, this.strokeWidth, this.radius);
                    break;
                }
                case Square.SHAPE_TYPE:{
                    output = new Square(context, this.projectName, this.x, this.y, this.strokeWidth, this.width, this.height);
                    break;
                }
                case Line.SHAPE_TYPE:{
                    output = new Line(context, this.projectName, this.x, this.y, this.strokeWidth, this.x2, this.y2);
                    break;
                }
                default: return null;
            }
            output.uniqueID = this.uniqueID;

            /*
            Log.i("save", output.shapeType+" : load");
            Log.i("save", output.drawX+" : load");
            Log.i("save", output.drawY+" : load");
            Log.i("save", output.x+" : load");
            Log.i("save", output.y+" : load");
            */

            return output;
        }
    }
}

