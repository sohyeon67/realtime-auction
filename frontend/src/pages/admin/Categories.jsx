import { Tree } from "react-arborist";
import api from "../../api/api";
import { useEffect, useRef, useState } from "react";
import { Box, Button, IconButton } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import "../../styles/Categories.css";
import ArrowRightIcon from '@mui/icons-material/ArrowRight';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import { useCategories } from "../../hooks/useCategories";

function CategoryAdminPage() {
  const treeRef = useRef();

  const {categories: originalCategories, refresh: refreshCategories} = useCategories();
  const [categories, setCategories] = useState([]);

  const [added, setAdded] = useState([]);
  const [updated, setUpdated] = useState([]);
  const [deleted, setDeleted] = useState([]);

  const [lastAddedId, setLastAddedId] = useState(null);

  useEffect(() => {
    const converted = convertIdsToString(originalCategories);
    setCategories(converted);
  }, [originalCategories]);

  const convertIdsToString = (nodes) =>
    nodes.map(node => ({
      ...node,
      id: String(node.id),
      children: node.children ? convertIdsToString(node.children) : [],
    }));

  const saveToServer = () => {
    const recalculated = recalcPriority(categories);

    api.post("/api/categories/batch", {
      added,
      updated: recalculated,
      deleted
    }).then(() => {
      setAdded([]);
      setUpdated([]);
      setDeleted([]);
      refreshCategories(); // 서버에서 다시 가져오기
    });
  };

  const handleCancel = () => {
    setCategories(originalCategories); // 원래 상태로 롤백
    setAdded([]);
    setUpdated([]);
    setDeleted([]);
  };

  // tree.create() 시 호출
  const handleCreate = ({ parentId, type }) => {
    const newNode = {
      id: `simple-tree-id-${Date.now()}`,
      name: "새 카테고리",
      children: type === "internal" ? [] : undefined,
      parentId: parentId || null,
    };

    // 트리 리렌더링
    setCategories(prev => {
      if (!parentId) return [...prev, newNode];
      return addNodeToParent(prev, parentId, newNode);
    });

    // 서버에 보낼 데이터 추가
    setAdded(prev => [...prev, { id: newNode.id, name: newNode.name }]);

    // 스크롤 후 edit상태로 바로 되지 않는 문제
    // setCategories는 비동기적 상태 업데이트라서 바로 다음 줄에서 
    // treeRef.current.get(newNode.id)를 호출하면 아직 트리에 반영되지 않은 상태일 수 있다. node가 null
    // 새로 추가된 노드id를 lastAddedId같은 state로 관리하고 그 값이 바뀌면 useEffect에서 node.edit()호출
    setLastAddedId(newNode.id);
  };

  // 노드 추가 렌더링 완료 후 스크롤 이동 및 편집 모드가 실행되도록 한다.
  useEffect(() => {
    if (lastAddedId) {
      treeRef.current.scrollTo(lastAddedId);
      const node = treeRef.current.get(lastAddedId);
      
      if (node) node.edit();
      setLastAddedId(null); // 한 번만 실행되게 리셋
    }
  }, [lastAddedId]);

  // node.submit() 시 호출
  const handleRename = ({ id, name }) => {
    // 트리 리렌더링
    setCategories(prev => renameNode(prev, id, name));

    // 서버에 보낼 데이터
    if (!id.startsWith("simple-tree-id")) { // 기존 노드
      setUpdated(prev => [...prev.filter(n => n.id !== id), { id, name }]);
    } else { // 새로 생성한 노드
      setAdded(prev => [...prev.filter(n => n.id !== id), { ...prev.find(n => n.id === id), name }]);
    }
  };

  // 드래그 시 호출
  const handleMove = ({ dragIds, parentId, index }) => {
    // 트리 리렌더링
    setCategories(prev => moveNodes(prev, dragIds, parentId, index));
  };

  // tree.delete() 시 호출
  const handleDelete = ({ ids }) => {
    // 트리 리렌더링
    setCategories(prev => deleteNodes(prev, ids));

    // 서버에 보낼 데이터
    const existingIds = ids.filter(id => !id.startsWith("simple-tree-id"));
    setDeleted(prev => [...prev, ...existingIds.map(id => ({ id }))]); // 기존에 있던 카테고리 id만 담는다.

    // added, updated에 있었다면 제거
    setAdded(prev => prev.filter(n => !ids.includes(n.id)));
    setUpdated(prev => prev.filter(n => !ids.includes(n.id)));
  };

  // 유틸리티 함수

  // 노드 추가 후 새 트리 데이터 반환
  const addNodeToParent = (nodes, parentId, newNode) =>
    nodes.map(node => {
      if (node.id === parentId) {
        const children = node.children ? [...node.children, newNode] : [newNode];
        return { ...node, children }; // children 업데이트
      }
      return node.children ? { ...node, children: addNodeToParent(node.children, parentId, newNode) } : node;
    });

  // 이름을 변경한 새 트리 데이터 반환
  const renameNode = (nodes, id, name) =>
    nodes.map(node => {
      if (node.id === id) return { ...node, name };
      return node.children ? { ...node, children: renameNode(node.children, id, name) } : node;
    });

  // 움직인 이후 새 트리 데이터 반환
  const moveNodes = (nodes, dragIds, parentId, index) => {
    const draggedNodes = [];
    // 드래그된 노드 제거
    const newTree = removeNodes(nodes, dragIds, draggedNodes);
    // 새로운 부모 아래에 삽입
    return insertNodes(newTree, parentId, draggedNodes, index);
  };

  // 드래그 한 노드(하위 포함) 제거한 트리 데이터 반환
  const removeNodes = (nodes, ids, draggedNodes) => {
    return nodes.filter(node => {
      if (ids.includes(node.id)) {
        draggedNodes.push(node);
        return false;
      }
      if (node.children) node.children = removeNodes(node.children, ids, draggedNodes);
      return true;
    });
  };

  // 드래그 한 노드(하위 포함) 삽입
  const insertNodes = (nodes, parentId, toInsert, index) => {
    // 루트 처리
    if (!parentId) {
      const newNodes = [...nodes];
      newNodes.splice(index, 0, ...toInsert);
      return newNodes;
    }

    return nodes.map(node => {
      // 부모 노드를 찾으면, 그 노드의 children 배열에 삽입
      if (node.id === parentId) {
        const children = node.children ? [...node.children] : [];
        children.splice(index, 0, ...toInsert);
        return { ...node, children };
      }

      // 부모 노드를 못찾았으면, 재귀 호출로 하위 노드 탐색
      if (node.children) node.children = insertNodes(node.children, parentId, toInsert, index);

      return node;
    });
  };

  // priority 재계산 함수
  const recalcPriority = (nodes, parentId = null) => {
    let result = [];
    nodes.forEach((node, idx) => {
      result.push({
        id: node.id,
        parentId,
        name: node.name,
        priority: idx + 1,
      });
      if (node.children?.length > 0) {
        result = result.concat(recalcPriority(node.children, node.id));
      }
    });
    return result;
  };

  // 특정 노드 삭제 후 트리 데이터 반환
  const deleteNodes = (nodes, ids) => nodes.filter(node => {
    if (ids.includes(node.id)) return false;
    if (node.children) node.children = deleteNodes(node.children, ids);
    return true;
  });


  const Node = ({ node, style, dragHandle, tree }) => {
    const hasChildren = node.children?.length > 0;
    const isSelected = tree.isSelected(node.id);

    return (
      <div
        className="node-container"
        style={style}
        ref={dragHandle}
      >

        <div
          className="node-content"
          onClick={() => !node.isEditing && node.isInternal && node.toggle()}
          style={style}>
          {hasChildren ? (node.isOpen ? <ArrowDropDownIcon /> : <ArrowRightIcon />) : null}
          {hasChildren ? "📂 " : "📄 "}
          {node.isEditing ? (
            <input
              type="text"
              defaultValue={node.data.name}
              autoFocus
              onFocus={e => e.currentTarget.select()}
              onBlur={() => node.reset()}
              onKeyDown={e => {
                if (e.key === "Enter") node.submit(e.currentTarget.value);
                if (e.key === "Escape") node.reset();
              }}
            />
          ) : (
            <span>{node.data.name}</span>
          )}
        </div>

        <div className="category-actions">
          <IconButton onClick={() => handleCreate({ parentId: node.id, type: "internal" })}>
            <AddIcon fontSize="small" color="primary" />
          </IconButton>
          <IconButton onClick={() => node.edit()}>
            <EditIcon fontSize="small" color="primary" />
          </IconButton>
          <IconButton onClick={() => handleDelete({ ids: [node.id] })}>
            <DeleteIcon fontSize="small" color="primary" />
          </IconButton>
        </div>

      </div>
    );
  };


  return (
    <>
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2, mt: 2 }}>

        <h2>카테고리 관리</h2>

        <Button
          variant="contained"
          onClick={() => handleCreate({})}
        >
        상위 카테고리 추가
        </Button>

        <div
          style={{
            border: "1px solid #ccc",
            width: "600px",
            height: "600px",
            overflowY: "auto"
          }}
        >
          <Tree
            ref={treeRef}
            data={categories}
            width={580}
            height={580}
            indent={24}
            rowHeight={32}

            onCreate={handleCreate}
            onRename={handleRename}
            onMove={handleMove}
            onDelete={handleDelete}
          >
            {Node}
          </Tree>
        </div>

        <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2 }}>
          <Button variant="outlined" onClick={saveToServer}>저장</Button>
          <Button variant="outlined" onClick={handleCancel}>취소</Button>
        </Box>
      </Box>
    </>
  );
}

export default CategoryAdminPage;
