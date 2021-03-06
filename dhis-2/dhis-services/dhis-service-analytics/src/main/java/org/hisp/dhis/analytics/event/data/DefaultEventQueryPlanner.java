package org.hisp.dhis.analytics.event.data;

/*
 * Copyright (c) 2004-2017, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.analytics.Partitions;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.analytics.QueryValidator;
import org.hisp.dhis.analytics.data.QueryPlannerUtils;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.EventQueryPlanner;
import org.hisp.dhis.analytics.AnalyticsTableType;
import org.hisp.dhis.analytics.table.PartitionUtils;
import org.hisp.dhis.common.MaintenanceModeException;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.program.ProgramIndicator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class DefaultEventQueryPlanner
    implements EventQueryPlanner
{
    @Autowired
    private QueryPlanner queryPlanner;
    
    @Autowired
    private QueryValidator queryValidator;

    // -------------------------------------------------------------------------
    // EventQueryPlanner implementation
    // -------------------------------------------------------------------------

    // TODO use list of functional groupers and single loop
    
    @Override
    public List<EventQueryParams> planAggregateQuery( EventQueryParams params )
    {
        List<EventQueryParams> queries = new ArrayList<>();
        
        List<EventQueryParams> groupedByQueryItems = groupByQueryItems( params );
        
        for ( EventQueryParams byQueryItem : groupedByQueryItems )
        {      
            List<EventQueryParams> groupedByOrgUnitLevel = QueryPlannerUtils.convert( queryPlanner.groupByOrgUnitLevel( byQueryItem ) );
            
            for ( EventQueryParams byOrgUnitLevel : groupedByOrgUnitLevel )
            {
                queries.addAll( QueryPlannerUtils.convert( queryPlanner.groupByPeriodType( byOrgUnitLevel ) ) );
            }
        }
        
        queries = withTableNameAndPartitions( queries );
        
        return queries;
    }

    @Override
    public EventQueryParams planEventQuery( EventQueryParams params )
    {
        return withTableNameAndPartitions( params );
    }
    
    public void validateMaintenanceMode()
        throws MaintenanceModeException
    {
        queryValidator.validateMaintenanceMode();
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Sets table name and partitions on the given query.
     * 
     * @param params the event query parameters.
     * @return a {@link EventQueryParams}.
     */
    private EventQueryParams withTableNameAndPartitions( EventQueryParams params )
    {
        Partitions partitions = params.hasStartEndDate() ?
            PartitionUtils.getPartitions( params.getStartDate(), params.getEndDate() ) :
            PartitionUtils.getPartitions( params.getAllPeriods() );
        
        String baseName = params.hasEnrollmentProgramIndicatorDimension() ?
            AnalyticsTableType.ENROLLMENT.getTableName() :
            AnalyticsTableType.EVENT.getTableName();
        
        String tableName = PartitionUtils.getTableName( baseName, params.getProgram() );
        
        return new EventQueryParams.Builder( params )
            .withTableName( tableName )
            .withPartitions( partitions )
            .build();
    }
    
    /**
     * Sets table name and partition on each query in the given list.
     * 
     * @param queries the list of queries.
     * @return a list of {@link EventQueryParams}.
     */
    private List<EventQueryParams> withTableNameAndPartitions( List<EventQueryParams> queries )
    {
        final List<EventQueryParams> list = new ArrayList<>();
        queries.forEach( query -> list.add( withTableNameAndPartitions( query ) ) );
        return list;
    }
    
    /**
     * Group by items if query items are to be collapsed in order to aggregate
     * each item individually. Sets program on the given parameters.
     * 
     * @param params the event query parameters.
     * @return a list of {@link EventQueryParams}.
     */
    private List<EventQueryParams> groupByQueryItems( EventQueryParams params )
    {
        List<EventQueryParams> queries = new ArrayList<>();
        
        if ( params.isAggregateData() )
        {
            for ( QueryItem item : params.getItemsAndItemFilters() )
            {
                EventQueryParams.Builder query = new EventQueryParams.Builder( params )
                    .removeItems()
                    .removeItemProgramIndicators()
                    .withValue( item.getItem() );
                
                if ( item.hasProgram() )
                {
                    query.withProgram( item.getProgram() );
                }
                
                queries.add( query.build() );
            }
            
            for ( ProgramIndicator programIndicator : params.getItemProgramIndicators() )
            {
                EventQueryParams query = new EventQueryParams.Builder( params )
                    .removeItems()
                    .removeItemProgramIndicators()
                    .withProgramIndicator( programIndicator )
                    .withProgram( programIndicator.getProgram() )
                    .build();
                
                queries.add( query );
            }
        }
        else if ( params.isCollapseDataDimensions() && !params.getItems().isEmpty() )
        {
            for ( QueryItem item : params.getItems() )
            {
                EventQueryParams.Builder query = new EventQueryParams.Builder( params )
                    .removeItems()
                    .addItem( item );
                
                if ( item.hasProgram() )
                {
                    query.withProgram( item.getProgram() );
                }
                
                queries.add( query.build() );
            }
        }
        else
        {
            queries.add( new EventQueryParams.Builder( params ).build() );
        }
        
        return queries;
    }
}
