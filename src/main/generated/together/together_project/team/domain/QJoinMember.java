package together.together_project.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJoinMember is a Querydsl query type for JoinMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJoinMember extends EntityPathBase<JoinMember> {

    private static final long serialVersionUID = -1905469437L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJoinMember joinMember = new QJoinMember("joinMember");

    public final together.together_project.domain.QBaseTimeEntity _super = new together.together_project.domain.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember joiner;

    public final QTeam team;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QJoinMember(String variable) {
        this(JoinMember.class, forVariable(variable), INITS);
    }

    public QJoinMember(Path<? extends JoinMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJoinMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJoinMember(PathMetadata metadata, PathInits inits) {
        this(JoinMember.class, metadata, inits);
    }

    public QJoinMember(Class<? extends JoinMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.joiner = inits.isInitialized("joiner") ? new QMember(forProperty("joiner"), inits.get("joiner")) : null;
        this.team = inits.isInitialized("team") ? new QTeam(forProperty("team"), inits.get("team")) : null;
    }

}

