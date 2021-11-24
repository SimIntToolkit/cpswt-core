package org.cpswt.hla;

import org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot;

import java.util.TreeMap;

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
    };

    private TreeMap< Integer, Filter > _handleFilterMap = new TreeMap< Integer, Filter >();

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

        boolean isSourceMapper = c2wInteractionRoot.get_sourceFed().endsWith( "Mapper" );
        boolean isFromSelf = c2wInteractionRoot.get_originFed() == federateId;

        return
         ( filter.getSourceFedFilter() == SourceFedFilter.MAPPER && !isSourceMapper ) ||
         ( filter.getSourceFedFilter() == SourceFedFilter.NON_MAPPER && isSourceMapper ) ||
         ( filter.getOriginFedFilter() == OriginFedFilter.SELF && !isFromSelf ) ||
         ( filter.getOriginFedFilter() == OriginFedFilter.NON_SELF && isFromSelf )
        ;

    }

};
