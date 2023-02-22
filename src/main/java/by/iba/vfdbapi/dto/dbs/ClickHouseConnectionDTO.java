package by.iba.vfdbapi.dto.dbs;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data Transfer Object Class, contains ClickHouse connection params.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ClickHouseConnectionDTO extends ConnectionDTO {
    private String host;
    private Integer port;
    private String database;
    private String user;
    private String password;
}
