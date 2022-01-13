package net.dragonbabyfly.discordchatbridge.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oroarmor.config.ConfigItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

// Copy from https://github.com/OroArmor/Oro-Config/blob/master/common/src/main/java/com/oroarmor/config/IntegerConfigItem.java
// Adapted for Long
public class LongConfigItem extends ConfigItem<Long> {
    protected Long min = Long.MIN_VALUE;
    protected Long max = Long.MAX_VALUE;

    public LongConfigItem(String name, Long defaultValue, String details) {
        super(name, defaultValue, details);
    }

    public LongConfigItem(String name, Long defaultValue, String details, @Nullable Consumer<ConfigItem<Long>> onChange) {
        super(name, defaultValue, details, onChange);
    }

    public LongConfigItem(String name, Long defaultValue, String details, @Nullable Consumer<ConfigItem<Long>> onChange, Long max) {
        super(name, defaultValue, details, onChange);
        this.max = max;
    }

    public LongConfigItem(String name, Long defaultValue, String details, @Nullable Consumer<ConfigItem<Long>> onChange, Long min, Long max) {
        super(name, defaultValue, details, onChange);
        this.min = min;
        this.max = max;
    }

    @Override
    public void fromJson(JsonElement element) {
        this.value = element.getAsLong();
    }

    @Override
    public void toJson(JsonObject object) {
        object.addProperty(this.name, this.value);
    }

    @Override
    public <T> boolean isValidType(Class<T> clazz) {
        return clazz == Long.class;
    }

    @Override
    public void setValue(Long value) {
        super.setValue(Long.max(Long.min(value, max), min));
    }

    @Override
    public String getCommandValue() {
        return this.value.toString();
    }

    /**
     * The min value for the config
     *
     * @return min
     */
    public Long getMin() {
        return min;
    }

    /**
     * The max value for the config
     *
     * @return max
     */
    public Long getMax() {
        return max;
    }
}
