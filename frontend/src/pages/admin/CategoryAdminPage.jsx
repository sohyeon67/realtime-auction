import { Tree } from "react-arborist";
import api from "../../api/api";
import { useEffect, useState } from "react";

function CategoryAdminPage() {
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        api.get("/categories")
        .then(res => {
            setCategories(res.data);
        })
        .catch(err => alert("카테고리 불러오기 실패"));
    }, []);

    const Node = ({ node, style, dragHandleProps }) => {
        let icon;
        switch(node.data.depth) {
            case 0:
                icon = "📁";
                break;
            default:
                icon = "📄";
        }

        return (
            <div style={{ ...style, background: 'aliceblue' }} >
                <span {...dragHandleProps} role="img" aria-label="folder">{icon}</span>
                <span>{node.data.name}</span>
                <button onClick={() => alert(node.data.id)}>Edit</button>
            </div>
        );
    };

    // 트리에서 노드 이동
  const moveNodes = (tree, dragIds, parentId, index) => {
    const movedNodes = [];

    const filterTree = (nodes) => {
      return nodes.filter(n => {
        if (dragIds.includes(n.id)) {
          movedNodes.push(n);
          return false;
        }
        if (n.children) n.children = filterTree(n.children);
        return true;
      });
    };

    const newTree = filterTree([...tree]);

    if (parentId === null) {
      newTree.splice(index, 0, ...movedNodes);
    } else {
      const insertToParent = (nodes) => {
        for (const n of nodes) {
          if (n.id === parentId) {
            n.children = n.children || [];
            n.children.splice(index, 0, ...movedNodes);
            return true;
          }
          if (n.children && insertToParent(n.children)) return true;
        }
        return false;
      };
      insertToParent(newTree);
    }

    return newTree;
  };

    const onCreate = ({ parentId, index, type }) => {};
    const onRename = ({ id, name }) => {};
    const onMove = ({ dragIds, parentId, index }) => {
        setCategories(prev => moveNodes(prev, dragIds, parentId, index));
    };
    const onDelete = ({ ids }) => {};

    return (
        <Tree
            data={categories}
            onMove={onMove}
            childrenAccessor="children"
            width={300}
            height={500}
            indent={24}
        >
            {Node}
        </Tree>
    );
}

export default CategoryAdminPage;