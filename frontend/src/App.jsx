import { BrowserRouter, Route, Routes } from 'react-router-dom'
import CategoryAdminPage from './pages/admin/CategoryAdminPage'

function App() {

  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path='/admin/categories' element={<CategoryAdminPage/>}/>
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App;
