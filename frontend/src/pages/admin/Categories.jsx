import { Tree } from "react-arborist";
import api from "../../api/api";
import { useEffect, useRef, useState } from "react";
import { IconButton } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

function CategoryAdminPage() {
  const treeRef = useRef(null);

  const [categories, setCategories] = useState([]);
  const [added, setAdded] = useState([]);
  const [updated, setUpdated] = useState([]);
  const [deleted, setDeleted] = useState([]);

  useEffect(() => {
    api.get("/api/categories")
      .then(res => {
        const converted = convertIdsToString(res.data);
        setCategories(converted);
      })
      .catch(err => alert("카테고리 불러오기 실패"));
  }, []);

  // 트리 데이터의 id가 string이어야 함
  const convertIdsToString = (nodes) => {
    return nodes.map(node => ({
      ...node,
      id: String(node.id),  // 숫자 -> 문자열
      children: node.children ? convertIdsToString(node.children) : []
    }));
  };

  // 서버에 변경사항 저장
  const saveToServer = () => {

    api.post("/api/categories/batch", {
      added,
      updated,
      deleted
    })
      .then(() => {
        setAdded([]);
        setUpdated([]);
        setDeleted([]);
      });
  };

  // 트리 노드 렌더링
  const Node = ({ node, style, dragHandle, tree }) => { // tree === treeRef.current
    // console.log(node);

    return (
      <div style={style} ref={dragHandle}>
        {(node.children && node.children.length > 0) ? "📂 " : "📄 "}
        {node.isEditing ? (
          <input
            type="text"
            defaultValue={node.data.name}
            autoFocus
            onFocus={(e) => e.currentTarget.select()}
            onBlur={() => node.reset()}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                node.submit(e.currentTarget.value);
                setUpdated(prev => [
                  ...prev,
                  { id: node.id, name: e.currentTarget.value }
                ]);
              }
              if (e.key === "Escape") node.reset();
            }}
          />
        ) : (
          <span>{node.data.name}</span>
        )}

        {/* 버튼 */}
        <IconButton onClick={() => {
          // 새 노드 생성
          const tempId = `new-${Date.now()}`;
          tree.create({
            parentId: node.id,
            index: node.children ? node.children.length : 0,
            data: { id: tempId, name: "New Node" }
          });
          setAdded(prev => [
            ...prev,
            { name: "New Node", parentId: node.id }
          ]);
        }}>
          <AddIcon fontSize="small" color="primary" />
        </IconButton>

        <IconButton onClick={() => node.edit()}>
          <EditIcon fontSize="small" color="primary" />
        </IconButton>

        <IconButton onClick={() => {
          tree.delete(node.id); // 화면에서 삭제
          setDeleted(prev => [...prev, { id: node.id }]);
          setAdded(prev => prev.filter(n => n.id !== node.id));
          setUpdated(prev => prev.filter(n => n.id !== node.id));
        }}>
          <DeleteIcon fontSize="small" color="primary" />
        </IconButton>
      </div>
    );
  };

  return (
    <div style={{
      border: "1px solid #ccc",
      borderRadius: "8px",
      padding: "8px",
      width: "450px",
      height: "510px",
      overflow: "auto"
    }}>
      <button onClick={saveToServer}>저장</button>
      {categories.length > 0 && // 데이터가 있을 때만 렌더링
        <Tree
          ref={treeRef} // arborist api 기능을 쓰기 위함?
          initialData={categories} // 초기 렌더링 한 번만 읽음 -> 이후 setCategories로 바꿔도 반영 안됨
          childrenAccessor="children"
          width={420}
          height={450}
          indent={24}
        >
          {Node}
        </Tree>
      }
    </div>
  );
}

export default CategoryAdminPage;
