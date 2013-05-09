/*
 * Copyright 2010-2012 Luca Garulli (l.garulli--at--orientechnologies.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orientechnologies.orient.core.index;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Proxied index.
 * 
 * @author Luca Garulli
 * 
 */
@SuppressWarnings("unchecked")
public class OIndexRemoteMultiValue extends OIndexRemote<Collection<OIdentifiable>> {
  protected final static String QUERY_GET = "select EXPAND( rid ) from index:%s where key = ?";

  public OIndexRemoteMultiValue(final String iName, final String iWrappedType, final ORID iRid,
      final OIndexDefinition iIndexDefinition, final ODocument iConfiguration, final Set<String> clustersToIndex) {
    super(iName, iWrappedType, iRid, iIndexDefinition, iConfiguration, clustersToIndex);
  }

  public Collection<OIdentifiable> get(final Object iKey) {
    final OCommandRequest cmd = formatCommand(QUERY_GET, name);
    return (Collection<OIdentifiable>) getDatabase().command(cmd).execute(iKey);
  }

  public Iterator<Entry<Object, Collection<OIdentifiable>>> iterator() {
    final OCommandRequest cmd = formatCommand(QUERY_ENTRIES, name);
    final Collection<ODocument> result = getDatabase().command(cmd).execute();

    final Map<Object, Collection<OIdentifiable>> map = new LinkedHashMap<Object, Collection<OIdentifiable>>();
    for (final ODocument d : result) {
      Collection<OIdentifiable> rids = map.get(d.field("key"));
      if (rids == null) {
        rids = new HashSet<OIdentifiable>();
        map.put(d.field("key"), rids);
      }

      rids.add((OIdentifiable) d.field("rid", OType.LINK));
    }

    return map.entrySet().iterator();
  }

  public Iterator<Entry<Object, Collection<OIdentifiable>>> inverseIterator() {
    final OCommandRequest cmd = formatCommand(QUERY_ENTRIES, name);
    final List<ODocument> result = getDatabase().command(cmd).execute();

    final Map<Object, Collection<OIdentifiable>> map = new LinkedHashMap<Object, Collection<OIdentifiable>>();
    for (ListIterator<ODocument> it = result.listIterator(); it.hasPrevious();) {
      ODocument d = it.previous();
      Collection<OIdentifiable> rids = map.get(d.field("key"));
      if (rids == null) {
        rids = new HashSet<OIdentifiable>();
        map.put(d.field("key"), rids);
      }

      rids.add((OIdentifiable) d.field("rid", OType.LINK));
    }

    return map.entrySet().iterator();
  }

  public Iterator<OIdentifiable> valuesIterator() {
    throw new UnsupportedOperationException();
  }

  public Iterator<OIdentifiable> valuesInverseIterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean supportsOrderedIterations() {
    return false;
  }
}
