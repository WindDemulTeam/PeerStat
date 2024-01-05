import React from "react";
import { useTable, useSortBy, useFilters, usePagination } from "react-table";
import classes from "./ReactTable.module.css";
import sort_desc from "../../img/sort_desc.png";
import sort_asc from "../../img/sort_asc.png";
import sort_both from "../../img/sort_both.png";

function ReactTable({ columns, data }) {
  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    page,
    prepareRow,
    pageCount,
    gotoPage,
    nextPage,
    previousPage,
    canNextPage,
    canPreviousPage,
    pageOptions,
    state: { pageIndex },
  } = useTable(
    {
      columns,
      data,
      disableSortRemove: true,
      initialState: { pageIndex: 0, pageSize: 40 },
    },
    useFilters,
    useSortBy,
    usePagination
  );

  return (
    <>
      <table className={classes.table} {...getTableProps()}>
        <thead>
          {headerGroups.map((headerGroup, index) => (
            <>
              <tr key={index} {...headerGroup.getHeaderGroupProps()}>
                {headerGroup.headers.map((column, index) => (
                  <th
                    key={index}
                    {...column.getHeaderProps(column.getSortByToggleProps())}
                    style={column.style}
                  >
                    <div className={classes.sorting}>
                      {column.render("Header")}
                      {column.isSorted ? (
                        column.isSortedDesc ? (
                          <img src={sort_desc} alt="desc" />
                        ) : (
                          <img src={sort_asc} alt="asc" />
                        )
                      ) : (
                        <img src={sort_both} alt="both" />
                      )}
                    </div>
                  </th>
                ))}
              </tr>
              <tr key={index}>
                {headerGroup.headers.map((column, index) => (
                  <th key={index} className={classes.filter}>
                    {column.canFilter ? column.render("Filter") : null}
                  </th>
                ))}
              </tr>
            </>
          ))}
        </thead>

        <tbody {...getTableBodyProps()}>
          {page.map((row, index) => {
            prepareRow(row);
            return (
              <tr key={index} {...row.getRowProps()}>
                {row.cells.map((cell, index) => (
                  <td key={index} {...cell.getCellProps()}>
                    {cell.render("Cell")}
                  </td>
                ))}
              </tr>
            );
          })}
        </tbody>
      </table>

      <div className={classes.pagination}>
        <button
          className={classes.button}
          onClick={() => gotoPage(0)}
          disabled={!canPreviousPage}
        >
          {"<<"}
        </button>{" "}
        <button
          className={classes.button}
          onClick={() => previousPage()}
          disabled={!canPreviousPage}
        >
          {"<"}
        </button>{" "}
        <button
          className={classes.button}
          onClick={() => nextPage()}
          disabled={!canNextPage}
        >
          {">"}
        </button>{" "}
        <button
          className={classes.button}
          onClick={() => gotoPage(pageCount - 1)}
          disabled={!canNextPage}
        >
          {">>"}
        </button>{" "}
        <span>
          Страница{" "}
          <strong>
            {pageIndex + 1} из {pageOptions.length}
          </strong>{" "}
        </span>
        <select
          value={pageIndex}
          onChange={(e) => {
            const page = e.target.value ? Number(e.target.value) : 0;
            gotoPage(page);
          }}
        >
          {pageOptions.map((option, index) => (
            <option key={index} value={index}>
              {index + 1}
            </option>
          ))}
        </select>{" "}
      </div>
    </>
  );
}

export default ReactTable;
