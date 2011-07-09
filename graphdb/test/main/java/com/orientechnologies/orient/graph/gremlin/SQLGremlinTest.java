package com.orientechnologies.orient.graph.gremlin;

import java.util.List;

import org.testng.annotations.Test;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OSQLEngine;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

public class SQLGremlinTest {
	@Test
	public void f() {
		OSQLEngine.getInstance().registerFunction(OSQLFunctionGremlin.NAME, OSQLFunctionGremlin.class);

		OGraphDatabase db = new OGraphDatabase("local:C:/temp/databases/gremlin");
		if (db.exists())
			db.open("admin", "admin");
		else
			db.create();

		// ODocument vertex1 = (ODocument) db.createVertex().field("label", "car").save();
		// ODocument vertex2 = (ODocument) db.createVertex().field("label", "pilot").save();
		// ODocument edge = (ODocument) db.createEdge(vertex1, vertex2).field("label", "drives").save();

		List<?> result = db.query(new OSQLSynchQuery<Object>("select gremlin('current.out.in') as value from V where outEdges.size() > 0 limit 3"));
		System.out.println("Result: " + result);

		db.close();
	}
}
