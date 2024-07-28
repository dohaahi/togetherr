package together.together_project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStudyPost is a Querydsl query type for StudyPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyPost extends EntityPathBase<StudyPost> {

    private static final long serialVersionUID = 782702373L;

    public static final QStudyPost studyPost = new QStudyPost("studyPost");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final DateTimePath<java.time.LocalDateTime> refreshedAt = createDateTime("refreshedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> studyPostId = createNumber("studyPostId", Long.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> totalLikeCount = createNumber("totalLikeCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QStudyPost(String variable) {
        super(StudyPost.class, forVariable(variable));
    }

    public QStudyPost(Path<? extends StudyPost> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudyPost(PathMetadata metadata) {
        super(StudyPost.class, metadata);
    }

}

