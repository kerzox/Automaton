package mod.kerzox.automaton.common.capabilities.heat;

public interface IHeat {

    /**
     * Mod uses degrees celsius for temperature
     */

    public static float ABSOLUTE_ZERO = -273.15f;
    public static float ROOM_TEMPERATURE = 20f;
    public static float BASE_STEAM_TEMPERATURE = 150f;

    void heat(float amount);
    void cool(float amount);
    void setTemperature(float temperature);
    float getTemperature();

}
