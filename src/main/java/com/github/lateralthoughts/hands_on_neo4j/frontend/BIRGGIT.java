package com.github.lateralthoughts.hands_on_neo4j.frontend;

import static com.google.common.base.Preconditions.checkNotNull;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lateralthoughts.hands_on_neo4j.domain.Branch;
import com.github.lateralthoughts.hands_on_neo4j.domain.Commit;
import com.github.lateralthoughts.hands_on_neo4j.domain.Project;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.*;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.CommitUtils;

/**
 * <motto>BIRGGIT, because YOLO!</motto>
 * Barely-Implemented and Roughly Graph-Based GIT.
 *
 * @author Florent Biville (@fbiville)
 * @author Olivier Girardot (@ogirardot)
 */
@Component
public final class BIRGGIT {

    private final GraphDatabaseService graphDB;
    private final LabelFinder labels;
    private final PropertyFinder properties;
    private final RelationTypeFinder relationshipTypes;
    private final UniqueIdentifierFinder uniqueIdentifiers;
    private final IndexFinder indices;
    private final CommitUtils commitUtils;

    @Autowired
    public BIRGGIT(GraphDatabaseService graphDB,
                   LabelFinder labels,
                   PropertyFinder properties,
                   RelationTypeFinder relationshipTypes,
                   UniqueIdentifierFinder uniqueIdentifiers,
                   IndexFinder indices,
                   CommitUtils commitUtils) {

        this.graphDB = graphDB;
        this.labels = labels;
        this.properties = properties;
        this.relationshipTypes = relationshipTypes;
        this.uniqueIdentifiers = uniqueIdentifiers;
        this.indices = indices;
        this.commitUtils = commitUtils;
    }

    /**
     * TODO
     *  1 - create node with labels {@link LabelFinder#findAllLabels(Class)}
     *  2 - set properties
     */
    public Node createNewProject(Project project) {
        checkNotNull(project);
        try (Transaction tx = graphDB.beginTx()) {
            Node node = null/* TODO */;
            tx.success();
            return node;
        }
    }
    
    /**
     * TODO
     *  1 - create node with labels {@link LabelFinder#findAllLabels(Class)}
     *  2 - set properties
     */
    public Node commit(Commit commit) {
        checkNotNull(commit);
        try (Transaction tx = graphDB.beginTx()) {
            Node node = null/*TODO*/;
            tx.success();
            return node;
        }
    }

    /**
     * TODO
     * 1 - clone node {@link this#cloneNode}
     * 2 - overwrite message
     */
    public Node amend(Node node, String newMessage) {
        checkNotNull(node);

        try (Transaction tx = graphDB.beginTx()) {
            Node newNode = null;

            tx.success();
            return newNode;
        }
    }

    /**
     * TODO
     * 1 - create nodes (project/commit)
     * 2 - create relationship
     * 2 - set properties
     */
    public Relationship createBranch(Branch newBranch) {
        checkNotNull(newBranch);

        try (Transaction tx = graphDB.beginTx()) {
            Relationship relationship = null/* TODO */;
            tx.success();
            return relationship;
        }
    }

    /**
     * 1 - TODO: create node with labels {@link LabelFinder#findAllLabels}
     *
     * 2 - TODO: duplicate properties
     * iterate on {@link Node#getPropertyKeys}
     * /!\ overwrite "identifier" property /!\
     *
     * 3 - TODO: duplicate relationships
     * iterate on {@link Node#getRelationships}
     *
     *  - 1st case : formerNode is start node
     *      clone.createRelationshipTo(...)
     *  - 2nd case : formerNode is end node
     *      currentRelationship.getEndNode().createRelationshipTo(clone,...)
     *  - finally : delete old relationship
     */
    private Node cloneNode(Node formerNode) {
        Node clone = null/*TODO*/;
        return clone;
    }
}
