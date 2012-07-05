package edu.umass.ciir.memindex;

import java.util.List;

public interface SearcherI {

	public abstract List<Result> runQuery(Query query) throws Exception;

}