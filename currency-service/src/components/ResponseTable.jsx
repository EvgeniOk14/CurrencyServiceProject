import React from 'react';
import '../css/CurrencySelector.css';

const ResponseTable = ({ data }) => {
    if (!data) return null;

    return (
        <div className="response-table">
            <h3>Результаты</h3>
            <p>Базовая валюта: {data.baseCurrency}</p>
            <p>Дата: {data.date}</p>
            <div className="response-table-container">
                <table>
                    <thead>
                    <tr>
                        <th>Код валюты</th>
                        <th>Курс</th>
                    </tr>
                    </thead>
                    <tbody>
                    {data.tableData.map(({ currency, rate }) => (
                        <tr key={currency}>
                            <td>{currency}</td>
                            <td>{rate}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ResponseTable;

// import React from 'react';
// import '../css/CurrencySelector.css';
//
// const ResponseTable = ({ data }) => {
//     if (!data) return null;
//
//     return (
//         <div className="response-table">
//             <h3>Результаты</h3>
//             <p>Базовая валюта: {data.baseCurrency}</p>
//             <p>Дата: {data.date}</p>
//             <table>
//                 <thead>
//                 <tr>
//                     <th>Код валюты</th>
//                     <th>Курс</th>
//                 </tr>
//                 </thead>
//                 <tbody>
//                 {data.tableData.map(({ currency, rate }) => (
//                     <tr key={currency}>
//                         <td>{currency}</td>
//                         <td>{rate}</td>
//                     </tr>
//                 ))}
//                 </tbody>
//             </table>
//         </div>
//     );
// };
//
// export default ResponseTable;
//
