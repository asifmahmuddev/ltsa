package uk.ac.ic.doc.scenebeans.animation.parse;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

class BeanUtil {
    static BeanInfo getBeanInfo(Class paramClass) throws AnimationParseException {
        try {
            return Introspector.getBeanInfo(paramClass);
        } catch (IntrospectionException introspectionException) {
            throw new AnimationParseException("could not find information about " + paramClass.getName() + " bean: " + introspectionException.getMessage());
        }
    }

    static BeanInfo getBeanInfo(Object paramObject) throws AnimationParseException {
        return getBeanInfo(paramObject.getClass());
    }

    static Object getProperty(Object paramObject, String paramString) throws AnimationParseException {
        BeanInfo beanInfo = getBeanInfo(paramObject);
        return getProperty(paramObject, beanInfo, paramString);
    }

    static Object getProperty(Object paramObject, BeanInfo paramBeanInfo, String paramString) throws AnimationParseException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(paramBeanInfo, paramString);
        try {
            Method method = propertyDescriptor.getReadMethod();
            return method.invoke(paramObject, new Object[0]);
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new AnimationParseException("cannot get " + paramString + " property of bean: " + exception.getMessage());
        }
    }

    static void setProperty(Object paramObject, BeanInfo paramBeanInfo, String paramString1, String paramString2, ValueParser paramValueParser) throws AnimationParseException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(paramBeanInfo, paramString1);
        Object object = paramValueParser.newObject(propertyDescriptor.getPropertyType(), paramString2);
        try {
            Method method = propertyDescriptor.getWriteMethod();
            if (method != null) {
                method.invoke(paramObject, new Object[]{object});
            } else {
                throw new AnimationParseException("attempted to set read-only property " + paramString1);
            }
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new AnimationParseException("cannot set " + paramString1 + " property of bean: " + exception.getMessage());
        }
    }

    static void setIndexedProperty(Object paramObject, BeanInfo paramBeanInfo, String paramString1, int paramInt, String paramString2, ValueParser paramValueParser) throws AnimationParseException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(paramBeanInfo, paramString1);
        if (!(propertyDescriptor instanceof IndexedPropertyDescriptor))
            throw new AnimationParseException("the " + paramString1 + " property is not indexed");
        IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor) propertyDescriptor;
        Object object = paramValueParser.newObject(indexedPropertyDescriptor.getIndexedPropertyType(), paramString2);
        try {
            Method method = indexedPropertyDescriptor.getIndexedWriteMethod();
            if (method != null) {
                method.invoke(paramObject, new Object[]{new Integer(paramInt), object});
            } else {
                throw new AnimationParseException("attempted to set read-only property " + paramString1);
            }
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new AnimationParseException("cannot set " + paramString1 + " property of bean: " + exception.getMessage());
        }
    }

    static PropertyDescriptor getPropertyDescriptor(BeanInfo paramBeanInfo, String paramString) throws AnimationParseException {
        PropertyDescriptor[] arrayOfPropertyDescriptor = paramBeanInfo.getPropertyDescriptors();
        for (byte b = 0; b < arrayOfPropertyDescriptor.length; b++) {
            if (arrayOfPropertyDescriptor[b].getName().equals(paramString))
                return arrayOfPropertyDescriptor[b];
        }
        throw new AnimationParseException("beans of type " + paramBeanInfo.getBeanDescriptor().getName() + " do not have a property named " + paramString);
    }

    static void bindEventListener(Object paramObject1, Object paramObject2) throws AnimationParseException {
        EventSetDescriptor eventSetDescriptor = findCompatibleEvent(paramObject1, paramObject2);
        Method method = eventSetDescriptor.getAddListenerMethod();
        try {
            method.invoke(paramObject2, new Object[]{paramObject1});
        } catch (Exception exception) {
            throw new AnimationParseException("failed to register event listener: " + exception.getMessage());
        }
    }

    static EventSetDescriptor findCompatibleEvent(Object paramObject1, Object paramObject2) throws AnimationParseException {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(paramObject2.getClass());
        } catch (IntrospectionException introspectionException) {
            throw new AnimationParseException("cannot find info about event source: " + introspectionException.getMessage());
        }
        EventSetDescriptor[] arrayOfEventSetDescriptor = beanInfo.getEventSetDescriptors();
        for (byte b = 0; b < arrayOfEventSetDescriptor.length; b++) {
            Class clazz = arrayOfEventSetDescriptor[b].getListenerType();
            if (clazz.isInstance(paramObject1))
                return arrayOfEventSetDescriptor[b];
        }
        throw new AnimationParseException("listener not compatible with event source");
    }
}
