package org.ccci.framework.cdi.resource;

import java.lang.annotation.Annotation;
import java.util.Set;

import com.google.common.base.Objects;

public class ResourceIdentifier<T>
{
    private final Class<T> resourceType;
    private final Set<Annotation> qualifiers;
    
    public ResourceIdentifier(Class<T> resourceType, Set<Annotation> qualifiers)
    {
        this.resourceType = resourceType;
        this.qualifiers = qualifiers;
    }

    
    public Class<T> getResourceType()
    {
        return resourceType;
    }

    public Set<Annotation> getQualifiers()
    {
        return qualifiers;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(resourceType, qualifiers);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourceIdentifier<?> other = (ResourceIdentifier<?>) obj;
        if (qualifiers == null)
        {
            if (other.qualifiers != null)
                return false;
        }
        else if (!qualifiers.equals(other.qualifiers))
            return false;
        if (resourceType == null)
        {
            if (other.resourceType != null)
                return false;
        }
        else if (!resourceType.equals(other.resourceType))
            return false;
        return true;
    }

    
}
