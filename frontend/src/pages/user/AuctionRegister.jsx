import api from "../../api/api";
import { useNavigate } from "react-router-dom";
import AuctionForm from "../../components/user/auction/AuctionForm";

export default function AuctionRegister() {

  const navigate = useNavigate();

  // 등록 처리 로직
  const handleCreate = (data, isMain) => {
    const formData = new FormData();
    formData.append("title", data.title);
    formData.append("categoryId", data.categoryId);
    formData.append("description", data.description);
    formData.append("startPrice", data.startPrice);
    formData.append("startTime", data.startTime);
    formData.append("endTime", data.endTime);

    data.images.forEach((img, index) => {
      formData.append(`files[${index}].file`, img.file);
      formData.append(`files[${index}].sortOrder`, index);
      formData.append(`files[${index}].isMain`, index === isMain);
    });

    api.post("/api/auctions", formData)
      .then(res => {
        const id = res.data.auctionId;
        navigate(`/user/auctions/${id}`);
      })
      .catch(err => console.error(err));
  };

  return (
    <AuctionForm
      mode="register"
      onSubmit={handleCreate}
    />
  )
}