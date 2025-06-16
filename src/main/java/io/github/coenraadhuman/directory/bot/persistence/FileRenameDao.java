package io.github.coenraadhuman.directory.bot.persistence;

import java.util.Optional;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.customizer.Bind;

@RegisterConstructorMapper(FileRenamed.class)
public interface FileRenameDao {

    @SqlQuery("""
        INSERT OR REPLACE INTO file_renamed (file_name, renamed_path)
        VALUES (:fileName, :renamedPath)
        RETURNING *
    """)
    FileRenamed insertOrReplace(@BindMethods FileRenamed fileRenamed);

    @SqlQuery("""
        SELECT * FROM file_renamed
        WHERE file_name = :file_name
    """)
    Optional<FileRenamed> findByFileName(@Bind("file_name") String fileName);

}
