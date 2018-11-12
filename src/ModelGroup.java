import com.jogamp.opengl.util.texture.Texture;
import graphicslib3D.Material;

import java.util.ArrayList;

public class ModelGroup {

    public String groupName;
    public String materialName;
    public Material material;
    public ArrayList<Float> triangleVerts;
    public ArrayList<Float> textureCoords;
    public ArrayList<Float> normals;
    public String texture;

    public ModelGroup(String groupName) {
        this.groupName = groupName;
        this.triangleVerts = new ArrayList<Float>();
        this.textureCoords = new ArrayList<Float>();
        this.normals = new ArrayList<Float>();
    }

    public int getNumVertices() { return (triangleVerts.size()/3); }

    public float[] getVertices()
    {	float[] p = new float[triangleVerts.size()];
        for(int i = 0; i < triangleVerts.size(); i++)
        {	p[i] = triangleVerts.get(i);
        }
        return p;
    }

    public float[] getTextureCoordinates()
    {	float[] t = new float[(textureCoords.size())];
        for(int i = 0; i < textureCoords.size(); i++)
        {	t[i] = textureCoords.get(i);
        }
        return t;
    }

    public float[] getNormals()
    {	float[] n = new float[(normals.size())];
        for(int i = 0; i < normals.size(); i++)
        {	n[i] = normals.get(i);
        }
        return n;
    }
}