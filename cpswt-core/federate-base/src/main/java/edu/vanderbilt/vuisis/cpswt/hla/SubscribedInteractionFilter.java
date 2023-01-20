/*
 * Certain portions of this software are Copyright (C) 2006-present
 * Vanderbilt University, Institute for Software Integrated Systems.
 *
 * Certain portions of this software are contributed as a public service by
 * The National Institute of Standards and Technology (NIST) and are not
 * subject to U.S. Copyright.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above Vanderbilt University copyright notice, NIST contribution
 * notice and this permission and disclaimer notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. THE AUTHORS OR COPYRIGHT HOLDERS SHALL NOT HAVE
 * ANY OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 */

package edu.vanderbilt.vuisis.cpswt.hla;

import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot_p.C2WInteractionRoot;

import java.util.TreeMap;

@SuppressWarnings("unused")
public class SubscribedInteractionFilter {

    public enum OriginFedFilter { ORIGIN_FILTER_DISABLED, SELF, NON_SELF }
    public enum SourceFedFilter { SOURCE_FILTER_DISABLED, MAPPER, NON_MAPPER }

    public static class Filter {
        private OriginFedFilter _originFedFilter;
        private SourceFedFilter _sourceFedFilter;

        public Filter() {
        	_originFedFilter = OriginFedFilter.ORIGIN_FILTER_DISABLED;
        	_sourceFedFilter = SourceFedFilter.SOURCE_FILTER_DISABLED;
        }

        void setOriginFedFilter( OriginFedFilter originFedFilter ) {
            _originFedFilter = originFedFilter;
        }
        OriginFedFilter getOriginFedFilter() {
        	return _originFedFilter;
        }

        void setSourceFedFilter( SourceFedFilter sourceFedFilter ) {
            _sourceFedFilter = sourceFedFilter;
        }
        SourceFedFilter getSourceFedFilter() {
        	return _sourceFedFilter;
        }
    }

    private final TreeMap< Integer, Filter > _handleFilterMap = new TreeMap<>();

    public void setOriginFedFilter( Integer handle, OriginFedFilter originFedFilter ) {
    	Filter filter = _handleFilterMap.get( handle );
        if ( filter == null ) {
            _handleFilterMap.put( handle, new Filter() );
            filter = _handleFilterMap.get( handle );
        }
        filter.setOriginFedFilter( originFedFilter );
    }

    public void setSourceFedFilter( int handle, SourceFedFilter sourceFedFilter ) {
    	Filter filter = _handleFilterMap.get( handle );
        if ( filter == null ) {
            _handleFilterMap.put( handle, new Filter() );
            filter = _handleFilterMap.get( handle );
        }
        filter.setSourceFedFilter( sourceFedFilter );
    }

    public void setFedFilters( int handle, OriginFedFilter originFedFilter, SourceFedFilter sourceFedFilter ) {
    	Filter filter = _handleFilterMap.get( handle );
        if ( filter == null ) {
            _handleFilterMap.put( handle, new Filter() );
            filter = _handleFilterMap.get( handle );
        }
        filter.setOriginFedFilter( originFedFilter );
        filter.setSourceFedFilter( sourceFedFilter );
    }
   
    //TODO: Find a good place for this  
    public boolean filterC2WInteraction( String federateId, C2WInteractionRoot c2wInteractionRoot ) {

        int handle = c2wInteractionRoot.getClassHandle();
        Filter filter = _handleFilterMap.get( handle );
        if ( filter == null ) {
            return false;
        }

        boolean isSourceMapper = c2wInteractionRoot.getSourceFederateId().endsWith( "Mapper" );
        boolean isFromSelf = c2wInteractionRoot.getOriginFederateId().equals(federateId);

        return
         ( filter.getSourceFedFilter() == SourceFedFilter.MAPPER && !isSourceMapper ) ||
         ( filter.getSourceFedFilter() == SourceFedFilter.NON_MAPPER && isSourceMapper ) ||
         ( filter.getOriginFedFilter() == OriginFedFilter.SELF && !isFromSelf ) ||
         ( filter.getOriginFedFilter() == OriginFedFilter.NON_SELF && isFromSelf )
        ;

    }

}
