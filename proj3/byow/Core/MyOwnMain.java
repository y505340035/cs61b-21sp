package byow.Core;

public class MyOwnMain {
    public static void main(String[] args) {
        Engine engine = new Engine(true);
//        TETile[][] world = engine.interactWithInputString(args[0]);
//        TERenderer teRenderer = new TERenderer();
//        teRenderer.initialize(world.length, world[0].length);
//        teRenderer.renderFrame(world);
        engine.interactWithKeyboard();
    }
}
