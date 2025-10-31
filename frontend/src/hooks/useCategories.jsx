import { useEffect, useState } from "react";
import api from "../api/api";

export const useCategories = () => {
  const [categories, setCategories] = useState([]);

  const fetchCategories = () => {
    api.get("/api/categories")
      .then(res => setCategories(res.data))
      .catch(() => alert("카테고리 불러오기 실패"));
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  return { categories, setCategories, refresh: fetchCategories };
};