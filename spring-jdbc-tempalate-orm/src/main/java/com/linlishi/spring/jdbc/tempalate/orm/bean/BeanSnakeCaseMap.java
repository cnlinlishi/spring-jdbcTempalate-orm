package com.linlishi.spring.jdbc.tempalate.orm.bean;

import java.security.ProtectionDomain;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.ReflectUtils;


public abstract class BeanSnakeCaseMap {
	
	public static final int REQUIRE_GETTER = 1;
	public static final int REQUIRE_SETTER = 2;
	
	
	public static BeanSnakeCaseMap create(Class<?> cls) {
        Generator gen = new Generator();
        gen.setBeanClass(cls);
        return gen.create();
    }
	
	abstract public BeanSnakeCaseMap newInstance();
	abstract public Object newBeanInstance();
	
	
	abstract public String[] getKeys();
	abstract public Object get(Object bean, String key);
	abstract public void put(Object bean, String key, Object value);
	
	public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(BeanMap.class.getName());

        private static final BeanMapKey KEY_FACTORY =
          (BeanMapKey)KeyFactory.create(BeanMapKey.class, KeyFactory.CLASS_BY_NAME);

        interface BeanMapKey {
            public Object newInstance(Class type, int require);
        }
        
        private Object bean;
        private Class beanClass;
        private int require;
        
        public Generator() {
            super(SOURCE);
        }

        /**
         * Set the bean that the generated map should reflect. The bean may be swapped
         * out for another bean of the same type using {@link #setBean}.
         * Calling this method overrides any value previously set using {@link #setBeanClass}.
         * You must call either this method or {@link #setBeanClass} before {@link #create}.
         * @param bean the initial bean
         */
        public void setBean(Object bean) {
            
            if (bean != null)
                beanClass = bean.getClass();
        }

        /**
         * Set the class of the bean that the generated map should support.
         * You must call either this method or {@link #setBeanClass} before {@link #create}.
         * @param beanClass the class of the bean
         */
        public void setBeanClass(Class beanClass) {
            this.beanClass = beanClass;
        }

        /**
         * Limit the properties reflected by the generated map.
         * @param require any combination of {@link #REQUIRE_GETTER} and
         * {@link #REQUIRE_SETTER}; default is zero (any property allowed)
         */
        public void setRequire(int require) {
            this.require = require;
        }

        protected ClassLoader getDefaultClassLoader() {
            return beanClass.getClassLoader();
        }

        protected ProtectionDomain getProtectionDomain() {
        	return ReflectUtils.getProtectionDomain(beanClass);
        }

        /**
         * Create a new instance of the <code>BeanMap</code>. An existing
         * generated class will be reused if possible.
         */
        public BeanSnakeCaseMap create() {
            if (beanClass == null)
                throw new IllegalArgumentException("Class of bean unknown");
            setNamePrefix(beanClass.getName());
            return (BeanSnakeCaseMap)super.create(KEY_FACTORY.newInstance(beanClass, require));
        }

        public void generateClass(ClassVisitor v) throws Exception {
            new BeanSnakeCaseMapEmitter(v, getClassName(), beanClass, require);
        }

        protected Object firstInstance(Class type) {
            return ((BeanSnakeCaseMap)ReflectUtils.newInstance(type)).newInstance();
        }

        protected Object nextInstance(Object instance) {
            return ((BeanSnakeCaseMap)instance).newInstance();
        }
    }
	
	public BeanSnakeCaseMap() {
    }
	

}
