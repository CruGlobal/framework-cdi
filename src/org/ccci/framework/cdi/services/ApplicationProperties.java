package org.ccci.framework.cdi.services;

import java.util.List;

public interface ApplicationProperties
{

    public abstract void loadProperties() throws Exception;

    public abstract String get(String module, String key);

    public abstract boolean getBoolean(String module, String key);

    public abstract float getFloat(String module, String key);

    public abstract List<String> getList(String module, String key);

    public abstract long getLong(String module, String key);

    public abstract String get(String module, String key, String defaultVal);

}
