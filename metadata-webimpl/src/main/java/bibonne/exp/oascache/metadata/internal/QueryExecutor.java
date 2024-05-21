package bibonne.exp.oascache.metadata.internal;

import lombok.NonNull;

public interface QueryExecutor{
    String execute(@NonNull String query);
}
