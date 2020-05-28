package com.linlishi.spring.jdbc.tempalate.orm.bean;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ObjectSwitchCallback;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;


public class BeanSnakeCaseMapEmitter extends ClassEmitter {
	private static final Type BEAN_MAP =
      TypeUtils.parseType("com.linlishi.spring.jdbc.tempalate.orm.bean.BeanSnakeCaseMap");
    private static final Signature CSTRUCT_OBJECT =
      TypeUtils.parseConstructor("Object");
    private static final Signature CSTRUCT_EMPTY =
    	      TypeUtils.parseConstructor(new Type[]{});
    private static final Signature BEAN_MAP_GET =
      TypeUtils.parseSignature("Object get(Object, String)");
    private static final Signature BEAN_MAP_PUT =
    		new Signature("put", Type.VOID_TYPE, new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_STRING, Constants.TYPE_OBJECT});
//      TypeUtils.parseSignature("void put(Object, String, Object)");
    private static final Signature KEYS =
      TypeUtils.parseSignature("String[] getKeys()");
    private static final Signature NEW_INSTANCE =
      new Signature("newInstance", BEAN_MAP, new Type[]{});
    private static final Signature NEW_BEAN_INSTANCE =
    		TypeUtils.parseSignature("Object newBeanInstance()");
    private static final Signature GET_PROPERTY_TYPE =
      TypeUtils.parseSignature("Class getPropertyType(String)");
	private static final String BOOLEAN_PRE_FIX = "is_";
	private static final String LOCAL_DATE_TIME_NAME = LocalDateTime.class.getName();
	private static final MethodInfo TRANS_OBJ_2_LOCAL_DATE_TIME;
	static {
		MethodInfo methodInfo = null;
		try {
//			methodInfo = ReflectUtils.getMethodInfo(CommonUtils.class.getMethod("transObj2LocalDateTime", Object.class));
		} catch (Exception e) {
			methodInfo = null;
		}
		TRANS_OBJ_2_LOCAL_DATE_TIME = methodInfo;
	}

	public BeanSnakeCaseMapEmitter(ClassVisitor v, String className, Class<?> type, int require) {
        super(v);

        begin_class(Constants.V1_8, Constants.ACC_PUBLIC, className, BEAN_MAP, null, Constants.SOURCE_FILE);
        EmitUtils.null_constructor(this);
        EmitUtils.factory_method(this, NEW_INSTANCE);
        generateConstructor();
        generateNewBeanInstance(type.getName());

        Map<String, PropertyDescriptor> getters = makePropertyMap(ReflectUtils.getBeanGetters(type));
        Map<String, PropertyDescriptor> setters = makePropertyMap(ReflectUtils.getBeanSetters(type));
        Map<String, PropertyDescriptor> allProps = new HashMap<>();
        allProps.putAll(getters);
        allProps.putAll(setters);

        if (require != 0) {
            for (Iterator<String> it = allProps.keySet().iterator(); it.hasNext();) {
                String name = (String)it.next();
                if ((((require & BeanSnakeCaseMap.REQUIRE_GETTER) != 0) && !getters.containsKey(name)) ||
                    (((require & BeanSnakeCaseMap.REQUIRE_SETTER) != 0) && !setters.containsKey(name))) {
                    it.remove();
                    getters.remove(name);
                    setters.remove(name);
                }
            }
        }
        generateGet(type, getters);
        generatePut(type, setters);

        String[] allNames = getNames(allProps);
        generateKeys(allNames);
        generateGetPropertyType(allProps, allNames);
        end_class();
    }

    private Map<String, PropertyDescriptor> makePropertyMap(PropertyDescriptor[] props) {
        Map<String, PropertyDescriptor> names = new HashMap<>();
        for (int i = 0; i < props.length; i++) {
        	PropertyDescriptor prop = (PropertyDescriptor)props[i];
        	String key = prop.getName();
        	char[] chars = key.toCharArray();
        	char[] newChars = new char[chars.length + 10];
        	if(Character.isUpperCase(chars[0])) {
        		chars[0] = Character.toLowerCase(chars[0]);
        	}
        	int diff = 0;
        	for (int j = 0; j < chars.length; j++) {
        		if(Character.isUpperCase(chars[j])) {
        			chars[j] = Character.toLowerCase(chars[j]);
        			newChars[j + diff++] = '_';
        		}
        		newChars[j + diff] = chars[j];
			}

        	key = new String(newChars, 0, chars.length + diff);
        	
        	if (prop.getPropertyType() == Boolean.TYPE) {
        		chars = key.toCharArray();
        		key = new String(chars, 0, chars.length);
        		key = BOOLEAN_PRE_FIX + key;
        	}
            names.put(key, props[i]);
        }
        return names;
    }

    private String[] getNames(Map<String, PropertyDescriptor> propertyMap) {
        return propertyMap.keySet().toArray(new String[propertyMap.size()]);
    }

    private void generateConstructor() {
        CodeEmitter e = begin_method(Constants.ACC_PUBLIC, CSTRUCT_OBJECT, null);
        e.load_this();
        e.load_arg(0);
        e.super_invoke_constructor(CSTRUCT_OBJECT);
        e.return_value();
        e.end_method();
    }

    private void generateNewBeanInstance(String className) {
    	Type beanType = TypeUtils.getType(className);
        CodeEmitter e = begin_method(Constants.ACC_PUBLIC, NEW_BEAN_INSTANCE, null);
        e.new_instance(beanType);
        e.dup();
        e.invoke_constructor(beanType, CSTRUCT_EMPTY);
        e.return_value();
        e.end_method();
    }

    private void generateGet(Class<?> type, final Map<String, PropertyDescriptor> getters) {
        final CodeEmitter e = begin_method(Constants.ACC_PUBLIC, BEAN_MAP_GET, null);
        e.load_arg(0);
        e.checkcast(Type.getType(type));
        e.load_arg(1);
        e.checkcast(Constants.TYPE_STRING);
        EmitUtils.string_switch(e, getNames(getters), Constants.SWITCH_STYLE_HASH, new ObjectSwitchCallback() {
            public void processCase(Object key, Label end) {
                PropertyDescriptor pd = (PropertyDescriptor)getters.get(key);
                MethodInfo method = ReflectUtils.getMethodInfo(pd.getReadMethod());
                e.invoke(method);
                e.box(method.getSignature().getReturnType());
                e.return_value();
            }
            public void processDefault() {
                e.aconst_null();
                e.return_value();
            }
        });
        e.end_method();
    }

    private void generatePut(Class<?> type, final Map<String, PropertyDescriptor> setters) {
        final CodeEmitter e = begin_method(Constants.ACC_PUBLIC, BEAN_MAP_PUT, null);
        e.load_arg(0);
        e.checkcast(Type.getType(type));
        e.load_arg(1);
        e.checkcast(Constants.TYPE_STRING);
        EmitUtils.string_switch(e, getNames(setters), Constants.SWITCH_STYLE_HASH, new ObjectSwitchCallback() {
            public void processCase(Object key, Label end) {
                PropertyDescriptor pd = (PropertyDescriptor)setters.get(key);
                if (pd.getReadMethod() == null) {
                    e.aconst_null();
                } else {
                    MethodInfo read = ReflectUtils.getMethodInfo(pd.getReadMethod());
                    e.dup();
                    e.invoke(read);
                    e.box(read.getSignature().getReturnType());
                }
                e.swap(); // move old value behind bean
                e.load_arg(2); // new value
                MethodInfo write = ReflectUtils.getMethodInfo(pd.getWriteMethod());
//                if (LOCAL_DATE_TIME_NAME.equals(pd.getPropertyType().getName())) {
//                	e.invoke(TRANS_OBJ_2_LOCAL_DATE_TIME);
//                }
                e.unbox(write.getSignature().getArgumentTypes()[0]);
                e.invoke(write);
                e.return_value();
            }
            public void processDefault() {
                // fall-through
            }
        });
        e.return_value();
        e.end_method();
    }

    private void generateKeys(String[] allNames) {
        CodeEmitter e = begin_method(Constants.ACC_PUBLIC, KEYS, null);
        EmitUtils.push_array(e, allNames);
        e.return_value();
        e.end_method();
    }

    private void generateGetPropertyType(final Map<String, PropertyDescriptor> allProps, String[] allNames) {
        final CodeEmitter e = begin_method(Constants.ACC_PUBLIC, GET_PROPERTY_TYPE, null);
        e.load_arg(0);
        EmitUtils.string_switch(e, allNames, Constants.SWITCH_STYLE_HASH, new ObjectSwitchCallback() {
            public void processCase(Object key, Label end) {
                PropertyDescriptor pd = allProps.get(key);
                EmitUtils.load_class(e, Type.getType(pd.getPropertyType()));
                e.return_value();
            }
            public void processDefault() {
                e.aconst_null();
                e.return_value();
            }
        });
        e.end_method();
    }
}
