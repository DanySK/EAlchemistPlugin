/***********************************************************************
 * Copyright (c) 2008 by SAP AG, Walldorf. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 ***********************************************************************/
package org.eclipse.jst.jee.model.internal.mergers;

import java.util.ArrayList;
import java.util.List;

/**
 * Base merger adapted to deal with Lists.
 * 
 * @author Dimitar Giormov
 *
 */
public class ModelElementsMerger implements IMerger {

    private List baseElements;
    private List toMergeList;
    
    /**
     * @param _baseList
     * @param _toMergeList
     */
    public ModelElementsMerger(List _baseList, List _toMergeList) {
        baseElements = _baseList;
        toMergeList = _toMergeList;
    }

    
    /**
     * @return
     */
    protected List getBaseElements() {
        return baseElements;
    }

    /**
     * @return
     */
    protected List getToMergeElemets() {
        return toMergeList;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jst.javaee.ejb.model.mergers.Merger#process()
     */
    public List process() throws ModelException {
        List warnings = new ArrayList(); 
        return warnings;
    }
}
