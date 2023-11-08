package ru.mrapple100.engine.scene;


import ru.mrapple100.engine.IGuiInstance;
import ru.mrapple100.engine.graph.Model;
import ru.mrapple100.engine.graph.TextureCache;

import java.util.HashMap;
import java.util.Map;

public class Scene {

    private Camera camera;
    private IGuiInstance guiInstance;
    private Map<String, Model> modelMap;
    private Projection projection;
    private SceneLights sceneLights;
    private TextureCache textureCache;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        camera = new Camera();
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }
        model.getEntitiesList().add(entity);
    }

    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    public void cleanup() {
        modelMap.values().forEach(Model::cleanup);
    }

    public Camera getCamera() {
        return camera;
    }

    public IGuiInstance getGuiInstance() {
        return guiInstance;
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }


    public Projection getProjection() {
        return projection;
    }

    public SceneLights getSceneLights() {
        return sceneLights;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }

    public void setGuiInstance(IGuiInstance guiInstance) {
        this.guiInstance = guiInstance;
    }

    public void setSceneLights(SceneLights sceneLights) {
        this.sceneLights = sceneLights;
    }
}
