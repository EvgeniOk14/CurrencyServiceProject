import React from 'react';
import Header from './components/Header.jsx';
import Footer from './components/Footer.jsx';
import CurrencySelector from './components/CurrencySelector.jsx';
import './App.css';

function App() {
    return (
        <div className="App">
            <Header />
            <main>
                <CurrencySelector />
            </main>
            <Footer />
        </div>
    );
}

export default App;
