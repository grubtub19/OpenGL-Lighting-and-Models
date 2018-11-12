import com.jogamp.opengl.GL4;

public class Square extends Shape {

    public Square(GL4 gl, float height, String texture) {
        genCoords(gl, height, texture);
    }

    public void genCoords(GL4 gl, float h, String texture) {
        float[] positionCoords = new float[0];
        float[] textureCoords = new float[0];
        float[] normalVectors = new float[0];
        float[][] result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3(-h, -h, h), //front
                new vec3( h, -h, h),
                new vec3( h,  h, h),
                new vec3(-h,  h, h));
        positionCoords = result[0];
        textureCoords = result[1];
        normalVectors = result[2];
        result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3( h, -h, h), //right
                new vec3( h, -h, -h),
                new vec3( h,  h, -h),
                new vec3( h,  h,  h));
        positionCoords = result[0];
        textureCoords = result[1];
        normalVectors = result[2];
        result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3(-h, -h, -h), //left
                new vec3(-h, -h,  h),
                new vec3(-h,  h,  h),
                new vec3(-h,  h, -h));
        positionCoords = result[0];
        textureCoords = result[1];
        normalVectors = result[2];
        result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3( h, -h, -h), //back
                new vec3(-h, -h, -h),
                new vec3(-h,  h, -h),
                new vec3( h,  h, -h));
        positionCoords = result[0];
        textureCoords = result[1];
        normalVectors = result[2];
        result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3(-h,  h,  h), //top
                new vec3( h,  h,  h),
                new vec3( h,  h, -h),
                new vec3(-h,  h, -h));
        positionCoords = result[0];
        textureCoords = result[1];
        normalVectors = result[2];
        result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3(-h, -h, -h), //bottom
                new vec3( h, -h, -h),
                new vec3( h, -h,  h),
                new vec3(-h, -h,  h));
        positionCoords = result[0];
        textureCoords = result[1];
        normalVectors = result[2];
        setup(gl, positionCoords, textureCoords, normalVectors, texture, Shape.defaultMaterial);
    }
}
