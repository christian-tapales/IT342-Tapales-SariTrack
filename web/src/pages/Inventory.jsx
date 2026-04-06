const Inventory = () => {
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-800">Product Inventory</h1>
        {/* ALIGNED: bg-[#16A394] */}
        <button className="bg-[#16A394] text-white px-4 py-2 rounded-lg hover:bg-[#0D7A6F]">
          + Add Product
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden border border-gray-100">
        <table className="w-full text-left">
          <thead className="bg-gray-50 border-b">
            <tr className="text-sm font-semibold text-gray-600">
              <th className="px-6 py-4">Product Name</th>
              <th className="px-6 py-4">Category</th>
              <th className="px-6 py-4">Price</th>
              <th className="px-6 py-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            <tr className="hover:bg-gray-50">
              <td className="px-6 py-4 text-gray-500">Lucky Me! Pancit Canton</td>
              <td className="px-6 py-4 text-gray-500">Noodles</td>
              <td className="px-6 py-4 text-gray-500 font-bold">₱15.00</td>
              <td className="px-6 py-4 text-right space-x-4">
                <button className="text-[#16A394] font-medium hover:underline">Edit</button>
                <button className="text-red-500 font-medium hover:underline">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Inventory;