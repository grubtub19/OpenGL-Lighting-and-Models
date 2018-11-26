import graphicslib3D.Material;

import java.io.*;
import java.util.ArrayList;

public class ModelImporter {

    public ArrayList<MaterialTexture> materialTextures = new ArrayList<MaterialTexture>();
    public ArrayList<ModelGroup> groups = new ArrayList<ModelGroup>();
    private ArrayList<Float> allVertices = new ArrayList<Float>();
    private ArrayList<Float> allTextureCoords = new ArrayList<Float>();
    private ArrayList<Float> allNormalVectors = new ArrayList<Float>();
    private String texture;

    /**
     * Parses the files given into multiple ModelGroups that contain verticies, materials, and textures.
     * @param objFilename path to .obj file
     * @param secondFilename path to either .mtl or .jpg
     * @param isMtl whether or not the second file is a .mtl
     */
    public ModelImporter(String objFilename, String secondFilename, boolean isMtl) {
        try {
            if(isMtl) {
                parseMDL(secondFilename);
                parseOBJ(objFilename);          //if the second file is a .mtl file, parse the mtl and obj files.
                linkMaterialsAndTextures();
            } else {
                System.out.println("It does not have a .mtl, instead, it uses a single texture file.");
                texture = secondFilename;
                parseOBJ(objFilename);          //if it's a single texture model, only parse the obj file

                linkMaterialsAndTextures();
            }
        } catch (IOException e) {
            System.err.println("Failed Loading ModelImporter Files: \n" + e);
        } catch (ModelException e) {
            System.out.println("Model Exception: " + e);
        }
    }

    class ModelException extends Exception {
        public ModelException(String s) {
            super(s);
        }
    }

    /**
     * Combination of a Material and a String. Probably unnecessary.
     */
    public class MaterialTexture {
        Material material;
        String texture;

        public MaterialTexture(String materialName) {
            material = new Material(materialName);
        }

        public String getName() {
            return material.getName();
        }
    }

    /**
     * Adds Material and texture properties to ModelGroups
     */
    private void linkMaterialsAndTextures() {
        if(texture != null) {
            //System.out.println("Using default texture");
            groups.get(0).texture = texture;
        }
        for (ModelGroup group : groups) {
            //System.out.println("Current Group Material: " + group.materialName);
            //System.out.println("Vert num: " + group.getNumVertices());
            for (MaterialTexture materialTexture : materialTextures) {
                //System.out.println("    comparing against: " + materialTexture.getName());
                if (group.materialName.equals(materialTexture.getName())) {

                    group.material = materialTexture.material;
                    group.texture = materialTexture.texture;
                    //System.out.println("        group: " + group.groupName + " get material: " + materialTexture.material.getName());
                    //System.out.println("        group: " + group.groupName + " has texture: " + group.texture);
                }
            }
        }
    }

    /**
     * Populates materialTextures ArrayList. Reads Ka (ambient), Kd (diffuse), Ks(specular), Ns (shinyness),
     * and map_Kd (texture) properties into them.
     * @param filename
     * @throws IOException
     */
    public void parseMDL(String filename) throws IOException, ModelException {
        File file = new File(filename);
        InputStream input = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String line;
        System.out.println("Starting MTL parse");
        while ((line = br.readLine()) != null) {                    //while we haven't reached the EOF
            System.out.println(line);
            if (line.startsWith("newmtl ")) {                       //if it's the start of a new Material definition
                System.out.println("Next Material:  " + line);
                materialTextures.add(new MaterialTexture(line.substring(7)));   //create an empty MaterialTexture
                while ((line = br.readLine()) != null && line.trim().length() > 0) {
                    //while we haven't reached EOF or an empty line
                    line = line.trim();                 //remove leading and trailing spaces

                    //System.out.println("line: " + line);
                    if (line.startsWith("Ka ")) {
                        String[] numbers = line.substring(3).trim().split(" ");    //numbers = array of numbers
                        materialTextures.get(materialTextures.size() - 1).material.setAmbient(new float[]
                                {Float.valueOf(numbers[0]), Float.valueOf(numbers[1]), Float.valueOf(numbers[2]), 1.0f}
                        );
                        //set the ambient value of the latest materialTexture to the numbers[0],[1],[2]
                    } else if (line.startsWith("Kd ")) {
                        String[] numbers = line.substring(3).trim().split(" ");    //numbers = array of numbers
                        materialTextures.get(materialTextures.size() - 1).material.setDiffuse(new float[]
                                {Float.valueOf(numbers[0]), Float.valueOf(numbers[1]), Float.valueOf(numbers[2]), 1.0f}
                        );
                        //set the diffuse value of the latest materialTexture to the numbers[0],[1],[2]
                    } else if (line.startsWith("Ks ")) {
                        String[] numbers = line.substring(3).trim().split(" ");    //numbers = array of numbers
                        materialTextures.get(materialTextures.size() - 1).material.setSpecular(new float[]
                                {Float.valueOf(numbers[0]), Float.valueOf(numbers[1]), Float.valueOf(numbers[2]), 1.0f}
                        );
                        //set the diffuse value of the latest materialTexture to the numbers[0],[1],[2]
                    } else if (line.startsWith("Ns ")) {
                        String shinyness = line.substring(3).trim().split(" ")[0]; //shininess = the very next number
                        materialTextures.get(materialTextures.size() - 1).material.setShininess(Float.valueOf(shinyness));
                        //set the shininess value to the first word on the right
                    } else if (line.startsWith("map_Kd")) {
                        String textureName = line.substring(7).trim(); //textureName = the very next word
                        materialTextures.get(materialTextures.size() - 1).texture = file.getParent() + "\\" + textureName;
                        System.out.println("materialTextures.get(" + (materialTextures.size() - 1) + ").texture = " + materialTextures.get(materialTextures.size() - 1).texture);
                        //set the texture filename to that of the .jpg file in the same directory as the .mtl
                    }
                }
            }
        }
        input.close();
    }

    public void parseOBJ(String filename) throws IOException, ModelException  {
        File file = new File(filename);
        InputStream input = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String line;
        String currMaterial = null;             //keep track of the most recent material name
        while ((line = br.readLine()) != null) {    //if we havent reached EOF
            //System.out.println("line: " + line);
            if (line.startsWith("v "))            // vertex position ("v" case)
            {
                //System.out.print("v -> line: " + line.substring(2));
                for (String s : (line.substring(2)).trim().split(" ")) {   //for each preceding number (expected: 3)
                    //System.out.println("s: *" + s + "*");
                    allVertices.add(Float.parseFloat(s)); //append it to the list of all verticies
                }
                //System.out.println();
            } else if (line.startsWith("vt"))            // texture coordinates ("vt" case)
            {
                //System.out.print("vt");
                for (String s : (line.substring(3)).trim().split(" ")) {   //for each preceding number (expected: 2)
                    //System.out.print(" " + s);
                    allTextureCoords.add(Float.valueOf(s));                //append it to the list of texture coordinates
                }
                //System.out.println();
            } else if (line.startsWith("vn"))            // vertex normals ("vn" case)
            {
                //System.out.print("vn");
                for (String s : (line.substring(3)).trim().split(" ")) {    //for each preceding number (expected: 3)
                    //System.out.print(" " + s);
                    allNormalVectors.add(Float.valueOf(s));                 //append it to the list of normal vectors
                }
                //System.out.println();
            } else if (line.startsWith("f"))            // triangle faces ("f" case)
            {
                if(groups.size() < 1) {                                     //if no group has been created,
                    //System.out.println("No group found, creating default");
                    groups.add(new ModelGroup("default"));       // make a new one named default
                }
                if(groups.get(groups.size() - 1).materialName == null) {    //if the current group doesn't have a material yet
                    groups.get(groups.size() - 1).materialName = currMaterial;  //make it the current material
                }
                else if(!groups.get(groups.size() - 1).materialName.equals(currMaterial)) {
                    // if the most recent group doesn't have the most recent material ie. no group has been defined
                    // since the last material change
                    ModelGroup temp = new ModelGroup("noName");
                    temp.materialName = currMaterial;       //add a new "noName" group with the current material
                    groups.add(temp);
                }
                //System.out.print("f");
                for (String s : (line.substring(2)).trim().split(" ")) {   //for each preceding word
                    //System.out.println("f ->line: " + s);
                    String[] split = s.split("/");
                    if(split.length < 3) {
                        throw new ModelException("Missing Normals");
                    }
                    String v = split[0];    // whole vertex location                //split the word by '/'
                    String vt = split[1];   // whole texture vertex location         //assign variables to the values
                    String vn = split[2];   // whole normal vector location

                    int vertRef = (Integer.valueOf(v) - 1) * 3;  //vertex array location
                    int tcRef = (Integer.valueOf(vt) - 1) * 2;   //texture array location
                    int normRef = (Integer.valueOf(vn) - 1) * 3; //normal array location


                    groups.get(groups.size() - 1).triangleVerts.add(allVertices.get(vertRef));          //add the single vertex
                    groups.get(groups.size() - 1).triangleVerts.add(allVertices.get((vertRef) + 1));    //to the array of vertices
                    groups.get(groups.size() - 1).triangleVerts.add(allVertices.get((vertRef) + 2));    //for the triangle faces
                    //System.out.println("Coordinates: " + allVertices.get(vertRef) + ", " + allVertices.get((vertRef) + 1) + ", " + allVertices.get((vertRef) + 2));

                    groups.get(groups.size() - 1).textureCoords.add(allTextureCoords.get(tcRef));       //add the single texture
                    groups.get(groups.size() - 1).textureCoords.add(allTextureCoords.get(tcRef + 1));   //coordinate to the array
                                                                                                        //of coordinates for the triangle faces
                    //System.out.println("TexCoords: " + allTextureCoords.get(tcRef) + ", " + allTextureCoords.get(tcRef + 1));

                    groups.get(groups.size() - 1).normals.add(allNormalVectors.get(normRef));       //add the single normal vector
                    groups.get(groups.size() - 1).normals.add(allNormalVectors.get(normRef + 1));   //to the array of normal vectors
                    groups.get(groups.size() - 1).normals.add(allNormalVectors.get(normRef + 2));   //for the triangle faces
                    //System.out.println("Normals: " + allNormalVectors.get(normRef) + ", " + allNormalVectors.get(normRef + 1) + ", " + allNormalVectors.get(normRef +2));
                }
                //System.out.println();
            } else if (line.startsWith("g ") | line.startsWith("o ")) { // group

                String groupName = line.substring(2); //groupName = the rest of the line
                //System.out.println("Creating group " + groupName);
                if(groups.size() > 0) {
                    if(groups.get(groups.size() - 1).getNumVertices() < 1) {
                        groups.remove(groups.size() - 1);
                    }
                    groups.add(new ModelGroup(groupName));  //add a new group to the list of all groups. This will be the
                    //group that is edited in the function from now on
                }
            } else if (line.startsWith("usemtl ")) {    //material
                System.out.println("use material: " + line.substring(7).split("\\s+")[0]);
                currMaterial = line.substring(7).split("\\s+")[0]; // currMaterial = the next word

            }
        }
        input.close();
        //System.out.println(groups.get(groups.size() - 1).triangleVerts.size());
    }
}
