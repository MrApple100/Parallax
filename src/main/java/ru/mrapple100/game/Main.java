package ru.mrapple100.game;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.mrapple100.FaceTrack;
import ru.mrapple100.engine.Engine;
import ru.mrapple100.engine.IAppLogic;
import ru.mrapple100.engine.MouseInput;
import ru.mrapple100.engine.Window;
import ru.mrapple100.engine.graph.Material;
import ru.mrapple100.engine.graph.Model;
import ru.mrapple100.engine.graph.Render;
import ru.mrapple100.engine.graph.Texture;
import ru.mrapple100.engine.scene.*;
import ru.mrapple100.light.PointLight;
import ru.mrapple100.light.SpotLight;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements IAppLogic {

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;

    private LightControls lightControls;

    private Entity cubeEntity;
    private Entity cubeEntity2;

    private Entity wallEntity;
    private Entity wallEntity2;
    private Entity wallEntity3;
    private Entity wallEntity4;
    private Entity wallEntity5;
    private Entity wallEntity6;
    private Entity wallEntity7;
    private Entity wallEntity8;
    private Entity wallEntity9;







    private float rotation;


    Camera camera;

    static Vector3f eyePos = new Vector3f(0.0f, 0.0f, 5.0f);;    // position of the view (eye position)
    static Vector3f targetPos = new Vector3f(0.0f, 0.0f, 0.0f); // the point which is looked at
    static Vector3f upVec = new Vector3f(0.0f, 1.0f, 0.0f);     // up vector of the view

    // Камера параллакса
    Matrix4f parallaxCameraView = new Matrix4f();
    Vector3f parallaxCameraPosition = new Vector3f(0.0f, 0.0f, 5.0f);


    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceTrack.runFaceTracking();
            }
        }).start();

        Main main = new Main();
        Engine gameEng = new Engine("chapter-11", new Window.WindowOptions(), main);
        gameEng.start();

    }

    @Override
    public void cleanup() {
        // Nothing to be done yet
    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        float[] positions = new float[]{
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,};


        Model cubeModel = ModelLoader.loadModel("cube-model", "resources/models/cube/cube.obj",
                scene.getTextureCache());
        scene.addModel(cubeModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 0f, -8);
        cubeEntity.updateModelMatrix();
        scene.addEntity(cubeEntity);

        cubeEntity2 = new Entity("cube-entity2", cubeModel.getId());
        cubeEntity2.setPosition(0, 0f, -10);
        cubeEntity2.updateModelMatrix();
        scene.addEntity(cubeEntity2);

        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.3f);
        scene.setSceneLights(sceneLights);
        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, 6), 1.0f));

        Vector3f coneDir = new Vector3f(0, 0, -8);
        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, 6), 1.0f), coneDir, 60.0f));

        lightControls = new LightControls(scene);
        scene.setGuiInstance(lightControls);

//        Texture texture = scene.getTextureCache().createTexture("resources/models/cube/cube.png");
//        Material material = new Material();
//        material.setTexturePath(texture.getTexturePath());
//        List<Material> materialList = new ArrayList<>();
//        materialList.add(material);

//        //wall
//        Texture texture2 = scene.getTextureCache().createTexture("resources/models/cube/cubeCell.png");
//        Material material2 = new Material();
//        material2.setTexturePath(texture2.getTexturePath());
//        List<Material> materialList2 = new ArrayList<>();
//        materialList2.add(material2);

//        Mesh mesh = new Mesh(positions, textCoords, indices);
//        material.getMeshList().add(mesh);
//        Mesh mesh2 = new Mesh(positions, textCoords, indices);
//        material2.getMeshList().add(mesh2);
//        Model cubeModel = new Model("cube-model", materialList);
//        scene.addModel(cubeModel);


//        cubeEntity = new Entity("cube-entity", cubeModel.getId());
//        cubeEntity.setPosition(0, 0, -8);
//        scene.addEntity(cubeEntity);

//        cubeEntity2 = new Entity("cube-entity", cubeModel.getId());
//        cubeEntity2.setPosition(0, 0, -10);
//        scene.addEntity(cubeEntity2);

        Model wallModel = cubeModel;
        scene.addModel(wallModel);

        wallEntity = new Entity("wall-entity", wallModel.getId());
        wallEntity.setPosition(0, 0, -20);
        wallEntity.setScale(10);
        scene.addEntity(wallEntity);

        wallEntity2 = new Entity("wall-entity2", wallModel.getId());
        wallEntity2.setPosition(0, -10, -10);
        wallEntity2.setScale(10);
        scene.addEntity(wallEntity2);

        wallEntity3 = new Entity("wall-entity3", wallModel.getId());
        wallEntity3.setPosition(0, 10, -10);
        wallEntity3.setScale(10);
        scene.addEntity(wallEntity3);

        wallEntity4 = new Entity("wall-entity4", wallModel.getId());
        wallEntity4.setPosition(10, 0, -10);
        wallEntity4.setScale(10);
        scene.addEntity(wallEntity4);

        wallEntity5 = new Entity("wall-entity5", wallModel.getId());
        wallEntity5.setPosition(-10, 0, -10);
        wallEntity5.setScale(10);
        scene.addEntity(wallEntity5);

        wallEntity6 = new Entity("wall-entity2", wallModel.getId());
        wallEntity6.setPosition(0, -10, 0);
        wallEntity6.setScale(10);
        scene.addEntity(wallEntity6);

        wallEntity7 = new Entity("wall-entity3", wallModel.getId());
        wallEntity7.setPosition(0, 10, 0);
        wallEntity7.setScale(10);
        scene.addEntity(wallEntity7);

        wallEntity8 = new Entity("wall-entity4", wallModel.getId());
        wallEntity8.setPosition(10, 0, 0);
        wallEntity8.setScale(10);
        scene.addEntity(wallEntity8);

        wallEntity9 = new Entity("wall-entity5", wallModel.getId());
        wallEntity9.setPosition(-10, 0, 0);
        wallEntity9.setScale(10);
        scene.addEntity(wallEntity9);

        //eyePos.add(0.1f,0,0);

    }
    boolean firstTime = true;

    float left = 10.0f;
    float right = -10.0f;
    float bottom = -10.0f;
    float top = 10.0f;
    float near = 7f;
    float far = 400.0f;
    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) {
            return;
        }
        float move = diffTimeMillis * MOVEMENT_SPEED;
         camera = scene.getCamera();
         if(firstTime){
             camera.eye(eyePos,targetPos,upVec);
             //scene.getProjection().getProjMatrix().setFrustum(left, right, bottom, top, near, far);
             firstTime = false;
         }

        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.moveUp(0.1f);
            parallaxCameraPosition.z -=0.1f;
          //  parallaxCameraPosition.y -=0.1f;


        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.moveDown(0.1f);
          //  parallaxCameraPosition.y +=0.1f;
            parallaxCameraPosition.z +=0.1f;


        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
          //  camera.moveLeft(move);
            parallaxCameraPosition.x += 0.1f;//; 3.0f * FaceTrack.xNorm.get();
            camera.moveLeft(-1f);
           // camera.eye(eyePos,targetPos,upVec);

            //  parallaxCameraPosition.sub(move,0,0);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
           // camera.moveRight(move);
            parallaxCameraPosition.x -=0.1f;
            camera.moveRight(-1f);
            //  parallaxCameraPosition.add(move,0,0);
        }
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            camera.moveUp(move);
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            camera.moveDown(move);
        }

        MouseInput mouseInput = window.getMouseInput();
        if (mouseInput.isRightButtonPressed()) {
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY),
                    (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
        }

    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        rotation += 1.5;
        if (rotation > 360) {
            rotation = 0;
        }
        cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        cubeEntity.updateModelMatrix();
        cubeEntity2.updateModelMatrix();


        //wallEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        wallEntity.updateModelMatrix();
        wallEntity2.updateModelMatrix();
        wallEntity3.updateModelMatrix();
        wallEntity4.updateModelMatrix();
        wallEntity5.updateModelMatrix();
        wallEntity6.updateModelMatrix();
        wallEntity7.updateModelMatrix();
        wallEntity8.updateModelMatrix();
        wallEntity9.updateModelMatrix();





        parallaxCameraPosition.x = 14.0f * FaceTrack.xNorm.get();//14 - это 2 * near
        parallaxCameraPosition.y = 14.0f * FaceTrack.yNorm.get();
        parallaxCameraPosition.z = FaceTrack.square.get();
       // System.out.println(camera.getPosition().x +" "+ (-10f * FaceTrack.xNorm.get()) + " "+ (camera.getPosition().x - (-10f * FaceTrack.xNorm.get())));
       // System.out.println(camera.getPosition().z + " " + ( FaceTrack.square.get()) + " " + (camera.getPosition().z - ( FaceTrack.square.get())));
          //  if(camera.getPosition().x - (5.0f * FaceTrack.xNorm.get())>0) {

        camera.moveLeft((camera.getPosition().x + (14f * FaceTrack.xNorm.get())));
        camera.moveDown((camera.getPosition().y + (14f * FaceTrack.yNorm.get())));

        float scale = 1f * parallaxCameraPosition.z;
        //  System.out.println(scale);
        left = scale * ( parallaxCameraPosition.x - 5.0f);
        right = scale * (parallaxCameraPosition.x + 5.0f); //5.0f это рамки пополам сцены которую надо видеть
        top = scale * (parallaxCameraPosition.y + 5.0f);
        bottom = scale * (parallaxCameraPosition.y - 5.0f);

// Обновите матрицу проекции в зависимости от изменения положения камеры взгляда
         scene.getProjection().getProjMatrix().setFrustum(left, right, bottom, top, near, far);
// Пересчитайте матрицу проекции с учетом изменений в угле обзора
        //float aspectRatio = (right - left) / (top - bottom);

       // float updatedFov = 60f / (7.0f + parallaxCameraPosition.z); // Изменение угла обзора
        //scene.getProjection().getProjMatrix().setPerspective(-updatedFov, -aspectRatio, near, far);

       // System.out.println(aspectRatio +" "+ updatedFov +" ");

    }
}
