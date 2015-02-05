/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.hk2.extras.hk2bridge.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

/**
 * @author jwells
 *
 */
public class CrossOverDescriptor<T> extends AbstractActiveDescriptor<T> {
    private final ServiceLocator remoteLocator;
    private final ActiveDescriptor<T> remote;
    private boolean remoteReified;
    
    public CrossOverDescriptor(ServiceLocator local, ActiveDescriptor<T> localService) {
        super(localService);
        
        this.remoteLocator = local;
        this.remote = localService;
        remoteReified = localService.isReified();
        
        setScopeAsAnnotation(remote.getScopeAsAnnotation());
        
        addMetadata(ExtrasUtilities.HK2BRIDGE_LOCATOR_ID, Long.toString(local.getLocatorId()));
        addMetadata(ExtrasUtilities.HK2BRIDGE_SERVICE_ID, Long.toString(localService.getServiceId()));
    }

    /**
     * This method returns true if this descriptor has been reified
     * (class loaded).  If this method returns false then the other methods
     * in this interface will throw an IllegalStateException.  Once this
     * method returns true it may be
     * 
     * @return true if this descriptor has been reified, false otherwise
     */
    public boolean isReified() {
        return true;
    }
    
    private synchronized void checkState() {
        if (remoteReified) return;
        remoteReified = true;
        
        if (remote.isReified()) return;
        remoteLocator.reifyDescriptor(remote);
    }
    
    @Override
    public Class<?> getImplementationClass() {
        checkState();
        
        return remote.getImplementationClass();
    }
    
    @Override
    public Set<Type> getContractTypes() {
        checkState();
        
        return remote.getContractTypes();
    }
    
    @Override
    public Annotation getScopeAsAnnotation() {
        checkState();
        
        return remote.getScopeAsAnnotation();
    }
    
    @Override
    public Class<? extends Annotation> getScopeAnnotation() {
        checkState();
        
        return remote.getScopeAnnotation();
    }
    
    @Override
    public Set<Annotation> getQualifierAnnotations() {
        checkState();
        
        return remote.getQualifierAnnotations();
    }
    
    @Override
    public List<Injectee> getInjectees() {
        checkState();
        
        return remote.getInjectees();
    }
    
    @Override
    public Long getFactoryServiceId() {
        checkState();
        
        return remote.getFactoryServiceId();
    }
    
    @Override
    public Long getFactoryLocatorId() {
        checkState();
        
        return remote.getFactoryLocatorId();
    }
    
    @Override
    public T create(ServiceHandle<?> root) {
        checkState();
        
        return remote.create(root);
    }
    
    @Override
    public void dispose(T instance) {
        checkState();
        
        remote.dispose(instance);
    }

}
