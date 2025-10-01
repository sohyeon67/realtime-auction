import { useNavigate, useParams } from 'react-router-dom';
import AuctionForm from '../../components/user/auction/AuctionForm';
import { useEffect, useState } from 'react';
import api from '../../api/api';

export default function AuctionEdit() {

  const { auctionId } = useParams();
  const navigate = useNavigate();
  const [initialData, setInitialData] = useState(null);

  // 수정 페이지 기존 데이터 불러오기
  useEffect(() => {
    api.get(`/api/auctions/${auctionId}`)
      .then(res => {
        const data = res.data;
        console.log(data);

        // 폼 입력값 세팅
        setInitialData({
          title: data.title,
          categoryId: data.categoryId,
          description: data.description,
          startPrice: data.startPrice,
          startTime: data.startTime,
          endTime: data.endTime,
          images: data.images || [],
        });
      })
      .catch(err => console.error(err));
  }, [auctionId]);

  // 수정 처리 로직
  const handleUpdate = (data, isMain) => {
    const formData = new FormData();
    formData.append("title", data.title);
    formData.append("categoryId", data.categoryId);
    formData.append("description", data.description);
    formData.append("startPrice", data.startPrice);
    formData.append("startTime", data.startTime);
    formData.append("endTime", data.endTime);

    // 기존 파일
    data.existingImages.forEach((img, index) => {
      formData.append(`existFiles[${index}].id`, img.id);
      formData.append(`existFiles[${index}].sortOrder`, index);
      formData.append(`existFiles[${index}].isMain`, index === isMain);
    });

    // 새로 업로드한 파일
    data.newImages.forEach((img, index) => {
      const sortOrder = data.existingImages.length + index;
      formData.append(`newFiles[${index}].file`, img.file);
      formData.append(`newFiles[${index}].sortOrder`, sortOrder);
      formData.append(`newFiles[${index}].isMain`, sortOrder === isMain);
    });


    api.patch(`/api/auctions/${auctionId}`, formData)
      .then(res => navigate(`/user/auctions/${auctionId}`))
      .catch(err => console.error(err));
  };

  if(!initialData) return <>loading...</>;

  return (
    <AuctionForm
      mode="edit"
      initialData={initialData}
      onSubmit={handleUpdate}
    />
  )
};