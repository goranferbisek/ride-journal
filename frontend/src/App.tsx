import { Outlet } from 'react-router'
import './App.css'

function App() {

  return (
    <>
      <section id="center">
        <Outlet/>
      </section>
    </>
  )
}

export default App
