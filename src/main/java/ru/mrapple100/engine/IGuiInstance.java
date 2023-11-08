package ru.mrapple100.engine;



import ru.mrapple100.engine.scene.Scene;

public interface IGuiInstance {
    void drawGui();

    boolean handleGuiInput(Scene scene, Window window);
}
