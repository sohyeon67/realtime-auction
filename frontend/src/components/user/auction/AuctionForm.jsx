import { Box, Button, Container, FormControl, Grid, IconButton, InputLabel, MenuItem, Select, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import DeleteIcon from "@mui/icons-material/Delete";
import api from "../../../api/api";

export default function AuctionForm({ initialData, onSubmit, mode = "register" }) {

  const {
    control,
    handleSubmit,
    setValue,
    formState: { isSubmitting, isSubmitted, errors },
    getValues
  } = useForm({
    defaultValues: {
      title: initialData?.title || "",
      categoryId: initialData?.categoryId || 1,
      description: initialData?.description || "",
      startPrice: initialData?.startPrice || 1000,
      startTime: initialData?.startTime || "",
      endTime: initialData?.endTime || "",
      images: initialData?.images || []
    }
  });

  const [existingImages, setExistingImages] = useState(initialData?.images || []);
  const [newImages, setNewImages] = useState([]);
  const [isMain, setIsMain] = useState(
    initialData?.images?.findIndex(img => img.isMain) ?? 0
  );

  const [categories, setCategories] = useState([]);

  useEffect(() => {
    api.get("/api/categories")
      .then(res => setCategories(res.data))
      .catch(() => alert("카테고리 불러오기 실패"));
  }, []);

  const flattenCategories = (categories, parentName = "") => {
    return categories.flatMap(cat => {
      const fullName = parentName ? `${parentName} > ${cat.name}` : cat.name;
      const items = [{ id: cat.id, name: fullName }];
      if (cat.children) {
        return [...items, ...flattenCategories(cat.children, fullName)];
      }
      return items;
    });
  }

  const flatCategoryList = flattenCategories(categories);

  // 새로운 파일 추가시
  const handleFilesChange = (e) => {
    // addImages : 파일 선택 이벤트에서 추가된 이미지들
    // newImages : 경매 수정하면서 새로 추가된 이미지들 목록
    const files = Array.from(e.target.files);
    const addImages = files.map((file, index) => ({
      file, // 파일 객체
      url: URL.createObjectURL(file), // 새 파일 url 생성
      isMain: false,
      sortOrder: existingImages.length + newImages.length + index,
    }));
    setNewImages((prev) => [...prev, ...addImages]);
    setValue("images", [...existingImages, ...newImages, ...addImages], { shouldValidate: true });
  };

  // 삭제
  const handleDelete = (index) => {
    const updated = [...existingImages, ...newImages].filter((_, i) => i !== index);
    setExistingImages(updated.filter(img => !img.file)); // 기존 이미지만
    setNewImages(updated.filter(img => img.file));  // 새 이미지만
    setValue("images", updated, { shouldValidate: true });
    if (isMain >= updated.length) setIsMain(0);
  };


  return (
    <Container
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
      }}
    >
      <form onSubmit={handleSubmit(data => onSubmit({ ...data, newImages, existingImages }, isMain))}>
        {/* 제목 */}
        <Controller
          name="title"
          control={control}
          rules={{
            required: "제목은 필수입니다.",
            maxLength: {
              value: 50,
              message: "제목은 50자 이하로 입력해주세요."
            }
          }}
          render={({ field }) => (
            <TextField
              {...field}
              label="상품 제목"
              variant="outlined"
              fullWidth
              error={!!errors.title}
              helperText={errors.title?.message}
              sx={{ mb: 3 }}
            />
          )}
        />

        {/* 카테고리 */}
        {categories.length > 0 && (
          <Controller
            name="categoryId"
            control={control}
            rules={{ required: "카테고리를 선택하세요." }}
            render={({ field }) => (
              <FormControl fullWidth variant="outlined" sx={{ mb: 1 }} error={!!errors.categoryId}>
                <InputLabel>카테고리</InputLabel>
                <Select {...field} label="카테고리">
                  {flatCategoryList.map(cat => (
                    <MenuItem key={cat.id} value={cat.id}>
                      {cat.name}
                    </MenuItem>
                  ))}
                </Select>
                {errors.categoryId && (
                  <Typography variant="caption" color="error">
                    {errors.categoryId.message}
                  </Typography>
                )}
              </FormControl>
            )}
          />
        )}

        {/* 설명 */}
        <Controller
          name="description"
          control={control}
          rules={{ required: "상품 설명은 필수입니다." }}
          render={({ field }) => (
            <TextField
              {...field}
              label="상품 설명"
              variant="outlined"
              fullWidth
              multiline
              minRows={4}
              sx={{ mt: 2 }}
              error={!!errors.description}
              helperText={errors.description?.message}
            />
          )}
        />

        {/* 시작가 */}
        <Controller
          name="startPrice"
          control={control}
          rules={{
            required: "경매 시작가 입력",
            min: { value: 1000, message: "최소 1000원 이상 입력" },
            max: { value: 10000000, message: "최대 1000만원 이하 입력" },
          }}
          render={({ field }) => (
            <TextField
              {...field}
              label="시작가"
              type="number"
              variant="outlined"
              error={!!errors.startPrice}
              helperText={errors.startPrice?.message}
              sx={{ mt: 2 }}
            />
          )}
        />

        <Grid container spacing={2} sx={{ mt: 2 }}>

          {/* 시작 시간 */}
          <Grid size={{ xs: 12, sm: 6 }}>

            <Controller
              name="startTime"
              control={control}
              rules={{
                required: "시작 시간을 입력하세요.",
                validate: (value) => {
                  const selected = new Date(value).getTime();
                  const now = new Date().getTime();
                  const plus90minutes = now + 90 * 60 * 1000;

                  return selected >= plus90minutes || "시작 시간은 현재로부터 최소 90분 후 이후여야 합니다.";
                }
              }}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="시작 시간"
                  type="datetime-local"
                  variant="outlined"
                  fullWidth
                  InputLabelProps={{ shrink: true }}
                  error={!!errors.startTime}
                  helperText={errors.startTime?.message}
                />
              )}
            />

          </Grid>


          {/* 종료 시간 */}
          <Grid size={{ xs: 12, sm: 6 }}>

            <Controller
              name="endTime"
              control={control}
              rules={{
                required: "종료 시간을 입력하세요.",
                validate: (value, formValues) => {
                  const start = new Date(formValues.startTime); // 시작 시간 필드와 비교
                  const end = new Date(value);

                  if (end <= start) return "종료는 시작 이후여야 합니다.";
                  if (end > new Date(start.getTime() + 10 * 24 * 60 * 60 * 1000))
                    return "종료 시간은 시작 후 10일 이내여야 합니다.";
                  return true;
                }
              }}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="종료 시간"
                  type="datetime-local"
                  variant="outlined"
                  fullWidth
                  InputLabelProps={{ shrink: true }}
                  error={!!errors.endTime}
                  helperText={errors.endTime?.message}
                />
              )}
            />

          </Grid>

        </Grid>

        {/* 이미지 업로드 */}
        <Controller
          name="images"
          control={control}
          rules={{ required: "이미지를 최소 1개 업로드하세요." }}
          render={() => {
            const allImages = [...existingImages, ...newImages];

            return (
              <Box sx={{ mt: 2 }}>
                <input
                  type="file"
                  multiple
                  accept="image/*"
                  style={{ display: "none" }}
                  id="file-upload"
                  onChange={handleFilesChange}
                />
                <label htmlFor="file-upload">
                  <Button variant="contained" component="span">
                    이미지 선택
                  </Button>
                </label>

                {errors.images && (
                  <Typography color="error" variant="caption" display="block">
                    {errors.images.message}
                  </Typography>
                )}

                {/* 미리보기 */}
                <Grid container spacing={2} sx={{ mt: 1 }}>
                  {allImages.map((file, index) => {

                    return (
                      <Grid key={index}>
                        <Box
                          sx={{
                            position: "relative",
                            border: isMain === index ? "2px solid #007FFF" : "1px solid #ccc",
                            borderRadius: 1,
                            width: 120,
                            height: 120,
                            overflow: "hidden",
                          }}
                        >
                          <img
                            src={file.url}
                            alt={`preview-${index}`}
                            style={{ width: "100%", height: "100%", objectFit: "cover", cursor: "pointer" }}
                            onClick={() => setIsMain(index)}
                          />
                          <IconButton
                            size="small"
                            onClick={() => handleDelete(index)}
                            sx={{ position: "absolute", top: 0, right: 0, color: "red" }}
                          >
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </Box>
                        <Typography variant="caption" display="block" textAlign="center">
                          {isMain === index ? "대표" : `${index + 1}`}
                        </Typography>
                      </Grid>
                    )
                  })}

                </Grid>

              </Box>
            );
          }}
        />

        <Button sx={{ mt: 2 }} type="submit" variant="contained" color="primary" fullWidth disabled={isSubmitting}>
          {mode === "register" ? "등록" : "수정"}
        </Button>

      </form>
    </Container>
  );
}