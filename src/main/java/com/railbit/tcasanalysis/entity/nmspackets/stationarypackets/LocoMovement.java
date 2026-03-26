//import React, { useState, useEffect, useCallback, useRef } from "react";
//import {
//  Box,
//  Typography,
//  Card,
//  CardContent,
//  Stack,
//  styled,
//  Table,
//  TableBody,
//  TableCell,
//  TableContainer,
//  TableHead,
//  TableRow,
//  Paper,
//  CircularProgress,
//  Button,
//  IconButton,
//  Tooltip,
//  Dialog,
//  DialogActions,
//  DialogContent,
//  Grid,
//  FormControlLabel,
//  Checkbox,
//  InputAdornment,
//  TextField,
//  MenuItem,
//  FormControl,
//  InputLabel,
//  Select,
//  Chip,
//} from "@mui/material";
//import { alpha, useTheme } from "@mui/material/styles";
//import {
//  TablePagination,
//  tablePaginationClasses as classes,
//} from "@mui/base/TablePagination";
//import {
//  LineChart,
//  Line,
//  XAxis,
//  YAxis,
//  CartesianGrid,
//  Tooltip as RechartsTooltip,
//  Legend,
//  ResponsiveContainer,
//  ReferenceLine,
//} from "recharts";
//import { LocalizationProvider } from '@mui/x-date-pickers';
//import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
//import { Train, Speed, Warning } from "@mui/icons-material";
//import FirstPageRoundedIcon from "@mui/icons-material/FirstPageRounded";
//import LastPageRoundedIcon from "@mui/icons-material/LastPageRounded";
//import ChevronLeftRoundedIcon from "@mui/icons-material/ChevronLeftRounded";
//import ChevronRightRoundedIcon from "@mui/icons-material/ChevronRightRounded";
//import FileDownloadOutlinedIcon from "@mui/icons-material/FileDownloadOutlined";
//import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
//import TravelExploreRoundedIcon from "@mui/icons-material/TravelExploreRounded";
//import ViewColumnRoundedIcon from "@mui/icons-material/ViewColumnRounded";
//import RefreshRoundedIcon from "@mui/icons-material/RefreshRounded";
//import AccessTimeRoundedIcon from "@mui/icons-material/AccessTimeRounded";
//import CloseIcon from "@mui/icons-material/Close";
//import RemoveRedEyeIcon from "@mui/icons-material/RemoveRedEye";
//import CalendarTodayRoundedIcon from "@mui/icons-material/CalendarTodayRounded";
//import XLSX from "xlsx";
//import axios from "axios.js";
//import { DateTimePicker } from "@mui/x-date-pickers";
//import dayjs from "dayjs";
//
//// ─── Design tokens ────────────────────────────────────────────────────────────
//const blue = { 200: "#A5D8FF", 400: "#3399FF" };
//const grey = {
//  50: "#F3F6F9",
//  200: "#DAE2ED",
//  300: "#C7D0DD",
//  600: "#6B7A90",
//  800: "#303740",
//  900: "#1C2025",
//};
//
//// ─── Helpers ──────────────────────────────────────────────────────────────────
//
///**
// * Build today's date at 00:00:00 (start of day)
// */
//const buildDefaultFrom = () => {
//  const d = new Date();
//  d.setHours(0, 0, 0, 0);
//  return d;
//};
//
///**
// * Build today's date at 23:59:59 (end of day)
// */
//const buildDefaultTo = () => {
//  const d = new Date();
//  d.setHours(23, 59, 59, 0);
//  return d;
//};
//
///** Date → "YYYY-MM-DD HH:mm:ss"  (API param) */
//const toApiDateTime = (date) => {
//  const p = (n) => String(n).padStart(2, "0");
//  return `${date.getFullYear()}-${p(date.getMonth() + 1)}-${p(date.getDate())} ${p(date.getHours())}:${p(date.getMinutes())}:${p(date.getSeconds())}`;
//};
//
///** Date → "DD/MM/YYYY HH:mm"  (chip display) */
//const toDisplayLabel = (date) => {
//  if (!date) return "";
//  const d = new Date(date);
//  const p = (n) => String(n).padStart(2, "0");
//  return `${p(d.getDate())}/${p(d.getMonth() + 1)}/${d.getFullYear()} ${p(d.getHours())}:${p(d.getMinutes())}`;
//};
//
///** ISO timestamp → "HH:mm:ss"  (chart x-axis) */
//const toTimeLabel = (ts) => {
//  if (!ts) return "";
//  const d = new Date(ts);
//  const p = (n) => String(n).padStart(2, "0");
//  return `${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`;
//};
//
//// ─── Packet type options ──────────────────────────────────────────────────────
//const PACKET_TYPE_OPTIONS = [
//  { value: "accessRequestPackets", label: "Access Request Packets" },
//  { value: "onboardRegularPackets", label: "Onboard Regular Packets" },
//];
//
//// ─── Static Alert Summary Configuration ───────────────────────────────────────
//// These are the 4 fixed alert types from your older code
//const STATIC_ALERT_CONFIG = [
//  {
//    label: "SOS Alerts",
//    color: "#c62828",
//    bg: "#ffebee",
//    // Match if emergencyStatusStr contains "sos" (case insensitive)
//    match: (status) => status?.toLowerCase().includes("sos")
//  },
//  {
//    label: "Emergency Brake",
//    color: "#e65100",
//    bg: "#fff3e0",
//    // Match if emergencyStatusStr contains "brake"
//    match: (status) => status?.toLowerCase().includes("brake")
//  },
//  {
//    label: "Head-on Collision",
//    color: "#c62828",
//    bg: "#ffebee",
//    // Match if emergencyStatusStr contains "collision"
//    match: (status) => status?.toLowerCase().includes("collision")
//  },
//  {
//    label: "Normal Operations",
//    color: "#2e7d32",
//    bg: "#e8f5e9",
//    // Match if emergencyStatusStr contains "no emergency" or is normal/regular
//    match: (status) => {
//      const s = status?.toLowerCase() || "";
//      return s.includes("no emergency") || s.includes("regular") || s.includes("normal");
//    }
//  },
//];
//
//// ─── Parent (top-level) columns — always shown ────────────────────────────────
//const parentColumns = [
//  { key: "_p_id", label: "ID", src: "parent" },
//  { key: "_p_locoId", label: "Loco ID", src: "parent" },
//  { key: "_p_atDate", label: "At Date", src: "parent" },
//  { key: "_p_pktType", label: "Pkt Type", src: "parent" },
//  { key: "_p_msgType", label: "Msg Type", src: "parent" },
//  { key: "_p_msgLength", label: "Msg Length", src: "parent" },
//  { key: "_p_msgSequence", label: "Msg Sequence", src: "parent" },
//  { key: "_p_kavachId", label: "Kavach ID", src: "parent" },
//  { key: "_p_nmsSystemId", label: "NMS System ID", src: "parent" },
//  { key: "_p_systemVersionStr", label: "System Version", src: "parent" },
//  { key: "_p_activeRadio", label: "Active Radio", src: "parent" },
//  { key: "_p_radioStatus", label: "Radio Status", src: "parent" },
//  { key: "_p_maSectionCount", label: "MA Section Count", src: "parent" },
//  { key: "_p_crcHex", label: "CRC Hex", src: "parent" },
//  { key: "_p_crcValid", label: "CRC Valid", src: "parent" },
//  { key: "_p_firm", label: "Firm", src: "parent" },
//  { key: "_p_isParsed", label: "Is Parsed", src: "parent" },
//  { key: "_p_createdAt", label: "Created At", src: "parent" },
//];
//
//// ─── Child (sub-packet) columns — switch by dropdown ─────────────────────────
//const accessRequestColumns = [
//  { key: "pktTypeStr", label: "Pkt Type Str", src: "child" },
//  { key: "pktLength", label: "Pkt Length", src: "child" },
//  { key: "frameNum", label: "Frame Num", src: "child" },
//  { key: "frameTime", label: "Frame Time", src: "child" },
//  { key: "sourceLocoId", label: "Source Loco ID", src: "child" },
//  { key: "sourceLocoVersionStr", label: "Loco Version", src: "child" },
//  { key: "absLocoLoc", label: "Abs Location", src: "child" },
//  { key: "trainLength", label: "Train Length", src: "child" },
//  { key: "trainSpeed", label: "Speed (km/h)", src: "child" },
//  { key: "movementDirStr", label: "Direction", src: "child" },
//  { key: "emergencyStatusStr", label: "Emergency Status", src: "child" },
//  { key: "locoModeStr", label: "Loco Mode", src: "child" },
//  { key: "approachingStnId", label: "Approaching Stn", src: "child" },
//  { key: "lastRfidTag", label: "Last RFID Tag", src: "child" },
//  { key: "tin", label: "TIN", src: "child" },
//  { key: "longitudeDeg", label: "Longitude", src: "child" },
//  { key: "latitudeDeg", label: "Latitude", src: "child" },
//  { key: "locoRndNum", label: "Loco Rnd Num", src: "child" },
//  { key: "pktCrc", label: "Packet CRC", src: "child" },
//];
//
//const onboardRegularColumns = [
//  { key: "pktTypeStr", label: "Pkt Type Str", src: "child" },
//  { key: "pktLength", label: "Pkt Length", src: "child" },
//  { key: "frameNum", label: "Frame Num", src: "child" },
//  { key: "frameTime", label: "Frame Time", src: "child" },
//  { key: "sourceLocoId", label: "Source Loco ID", src: "child" },
//  { key: "sourceLocoVersionStr", label: "Loco Version", src: "child" },
//  { key: "absLocoLoc", label: "Abs Location", src: "child" },
//  { key: "trainLength", label: "Train Length", src: "child" },
//  { key: "trainSpeed", label: "Speed (km/h)", src: "child" },
//  { key: "movementDirStr", label: "Direction", src: "child" },
//  { key: "emergencyStatusStr", label: "Emergency Status", src: "child" },
//  { key: "locoModeStr", label: "Loco Mode", src: "child" },
//  { key: "approachingStnId", label: "Approaching Stn", src: "child" },
//  { key: "lastRfidTag", label: "Last RFID Tag", src: "child" },
//  { key: "tin", label: "TIN", src: "child" },
//  { key: "longitudeDeg", label: "Longitude", src: "child" },
//  { key: "latitudeDeg", label: "Latitude", src: "child" },
//  { key: "locoRndNum", label: "Loco Rnd Num", src: "child" },
//  { key: "pktCrc", label: "Packet CRC", src: "child" },
//];
//
//const colWidthMapPackets = {
//  _p_id: 70,
//  _p_locoId: 90,
//  _p_atDate: 170,
//  _p_pktType: 110,
//  _p_msgType: 90,
//  _p_msgLength: 100,
//  _p_msgSequence: 120,
//  _p_kavachId: 100,
//  _p_nmsSystemId: 110,
//  _p_systemVersionStr: 130,
//  _p_activeRadio: 110,
//  _p_radioStatus: 140,
//  _p_maSectionCount: 130,
//  _p_crcHex: 120,
//  _p_crcValid: 100,
//  _p_firm: 70,
//  _p_isParsed: 90,
//  _p_createdAt: 170,
//  pktTypeStr: 170,
//  pktLength: 90,
//  frameNum: 100,
//  frameTime: 100,
//  sourceLocoId: 110,
//  sourceLocoVersionStr: 150,
//  absLocoLoc: 120,
//  trainLength: 110,
//  trainSpeed: 110,
//  movementDirStr: 160,
//  emergencyStatusStr: 230,
//  locoModeStr: 160,
//  approachingStnId: 130,
//  lastRfidTag: 110,
//  tin: 70,
//  longitudeDeg: 110,
//  latitudeDeg: 110,
//  locoRndNum: 110,
//  pktCrc: 110,
//};
//
//// ─── Styled Components ────────────────────────────────────────────────────────
//const Container = styled("div")(({ theme }) => ({
//  marginLeft: "30px",
//  marginRight: "30px",
//  marginTop: "20px",
//  paddingBottom: "24px",
//  [theme.breakpoints.down("sm")]: { margin: "16px" },
//}));
//
//const StyledTable = styled(Table)(({ theme }) => ({
//  width: "max-content",
//  minWidth: "100%",
//  tableLayout: "fixed",
//  borderCollapse: "separate",
//  borderSpacing: 0,
//  "& thead th": {
//    position: "sticky",
//    top: 0,
//    zIndex: 2,
//    background:
//      theme.palette.mode === "dark"
//        ? alpha(theme.palette.background.paper, 0.96)
//        : "linear-gradient(180deg, rgba(15,23,42,0.97) 0%, rgba(30,64,175,0.97) 100%)",
//    color: theme.palette.common.white,
//    fontSize: "0.76rem",
//    fontWeight: 700,
//    letterSpacing: "0.06em",
//    textTransform: "uppercase",
//    textAlign: "center",
//    borderBottom: `1px solid ${alpha(theme.palette.common.white, 0.12)}`,
//    padding: theme.spacing(1.45, 1),
//    whiteSpace: "normal",
//    lineHeight: 1.35,
//    wordBreak: "break-word",
//  },
//  "& thead th:first-of-type": { borderTopLeftRadius: 24 },
//  "& thead th:last-of-type": { borderTopRightRadius: 24 },
//  "& tbody td": {
//    padding: theme.spacing(1.25, 1),
//    borderBottom: `1px solid ${alpha(theme.palette.divider, 0.9)}`,
//    verticalAlign: "middle",
//    textAlign: "center",
//  },
//  [theme.breakpoints.down("md")]: {
//    minWidth: 860,
//    "& thead th": { fontSize: "0.71rem" },
//    "& tbody td": { padding: theme.spacing(1.05, 0.85), fontSize: "0.88rem" },
//  },
//}));
//
//const CustomTablePagination = styled(TablePagination)(
//  ({ theme }) => `
//  & .${classes.spacer} { display: none; }
//  & .${classes.toolbar} {
//    display: flex; flex-direction: column; align-items: flex-start;
//    gap: 8px; padding: 0; background-color: transparent;
//    @media (min-width: 768px) { flex-direction: row; align-items: center; }
//  }
//  & .${classes.selectLabel}, & .${classes.displayedRows} { margin: 0; font-size: 0.86rem; }
//  & .${classes.select} {
//    font-family: inherit; padding: 4px 24px 4px 8px;
//    border: 1px solid ${theme.palette.mode === "dark" ? grey[800] : grey[200]};
//    border-radius: 10px;
//    background-color: ${alpha(theme.palette.background.paper, 0.85)};
//    color: ${theme.palette.mode === "dark" ? grey[300] : grey[900]};
//  }
//  & .${classes.actions} { display: flex; gap: 8px; border: transparent; }
//  & .${classes.actions} > button {
//    width: 34px; height: 34px; padding: 0; border-radius: 50%;
//    background-color: transparent;
//    border: 1px solid ${theme.palette.mode === "dark" ? grey[800] : grey[200]};
//    color: ${theme.palette.mode === "dark" ? grey[300] : grey[900]};
//  }
//  & .${classes.actions} > button:focus {
//    outline: 3px solid ${theme.palette.mode === "dark" ? blue[400] : blue[200]};
//    border-color: ${blue[400]};
//  }
//`,
//);
//
//// ─── Emergency status colour mapping ─────────────────────────────────────────
//const getEmergencyColor = (status) => {
//  if (!status) return null;
//  const s = status.toLowerCase();
//  if (s.includes("no emergency")) return { color: "#2e7d32", bg: "#e8f5e9" };
//  if (s.includes("sos")) return { color: "#c62828", bg: "#ffebee" };
//  if (s.includes("brake")) return { color: "#e65100", bg: "#fff3e0" };
//  if (s.includes("collision")) return { color: "#c62828", bg: "#ffebee" };
//  return { color: "#e65100", bg: "#fff3e0" };
//};
//
//// ─── Component ────────────────────────────────────────────────────────────────
//const LocoMovementLive = ({ filterType }) => {
//  const { palette } = useTheme();
//  const bgPrimary = palette.primary.main;
//  const bgSuccess = palette.success.main;
//  const isDark = palette.mode === "dark";
//
//  const getFormattedDateTime = () => {
//    const n = new Date();
//    const p = (v) => String(v).padStart(2, "0");
//    return `${p(n.getDate())}-${p(n.getMonth() + 1)}-${n.getFullYear()} ${p(n.getHours())}:${p(n.getMinutes())}:${p(n.getSeconds())}`;
//  };
//
//  // ── State ──────────────────────────────────────────────────────────────────
//  const [locomotives, setLocomotives] = useState([]);
//  const [locoLoading, setLocoLoading] = useState(false);
//  const [selectedLocoNo, setSelectedLocoNo] = useState(null);
//
//  const [fromDate, setFromDate] = useState(buildDefaultFrom);
//  const [toDate, setToDate] = useState(buildDefaultTo);
//
//  const [telemetryLoading, setTelemetryLoading] = useState(false);
//  const [speedChartData, setSpeedChartData] = useState([]);
//  const [avgSpeed, setAvgSpeed] = useState(null);
//  const [activeLocoCount, setActiveLocoCount] = useState("-");
//
//  const [packetsLoading, setPacketsLoading] = useState(false);
//  const [packetsRawData, setPacketsRawData] = useState([]);
//  const [packetType, setPacketType] = useState("accessRequestPackets");
//  const [tableRows, setTableRows] = useState([]);
//  const [filteredRows, setFilteredRows] = useState([]);
//  const [alertSummaryData, setAlertSummaryData] = useState([]);
//  const [warningCount, setWarningCount] = useState(0);
//
//  const [page, setPage] = useState(0);
//  const [rowsPerPage, setRowsPerPage] = useState(7);
//  const [searchQuery, setSearchQuery] = useState("");
//  const [selectedRow, setSelectedRow] = useState(null);
//  const [dialogOpen, setDialogOpen] = useState(false);
//  const [currentDateTime, setCurrentDateTime] = useState(getFormattedDateTime());
//  const [excelLoading, setExcelLoading] = useState(false);
//  const [openColFilter, setOpenColFilter] = useState(false);
//
//  const [selectedColumns, setSelectedColumns] = useState([
//    ...parentColumns.map((c) => c.key),
//    ...accessRequestColumns.map((c) => c.key),
//  ]);
//
//  const serviceToken = JSON.parse(localStorage.getItem("token"));
//  const authHeader = {
//    Authorization: `Bearer ${serviceToken}`,
//    "Content-Type": "application/json",
//  };
//
//  // ── 1. Fetch locomotive list, auto-select first ───────────────────────────
//  const fetchLocomotives = async () => {
//    try {
//      setLocoLoading(true);
//      const res = await axios.get("/loco/", { headers: authHeader });
//      if (res.status === 200) {
//        const list = res.data.data ?? [];
//        setLocomotives(list);
//        if (list.length > 0) {
//          setSelectedLocoNo(list[0].nmsLocoId ?? list[0].locoNo);
//        }
//      }
//    } catch (err) {
//      console.error("Loco list API failed:", err);
//    } finally {
//      setLocoLoading(false);
//    }
//  };
//
//  useEffect(() => {
//    fetchLocomotives();
//  }, []);
//
//  // ── 2. Fetch telemetry (speed chart + avg speed) ──────────────────────────
//  const fetchTelemetry = useCallback(async () => {
//    if (!selectedLocoNo) return;
//    try {
//      setTelemetryLoading(true);
//      const params = {
//        fromDate: toApiDateTime(fromDate),
//        toDate: toApiDateTime(toDate),
//        locoId: selectedLocoNo,
//      };
//      const res = await axios.get("/loco/telemetry", {
//        params,
//        headers: authHeader,
//      });
//      if (res.status === 200) {
//        const records = res.data.data ?? [];
//        const sorted = [...records].sort(
//          (a, b) => new Date(a.timestamp) - new Date(b.timestamp),
//        );
//        setSpeedChartData(
//          sorted.map((r) => ({
//            time: toTimeLabel(r.timestamp),
//            speed: r.trainSpeed ?? 0,
//          })),
//        );
//        if (records.length > 0) {
//          const sum = records.reduce((acc, r) => acc + (r.trainSpeed ?? 0), 0);
//          setAvgSpeed((sum / records.length).toFixed(1));
//        } else {
//          setAvgSpeed("0.0");
//        }
//        setActiveLocoCount(new Set(records.map((r) => r.locoId)).size);
//      }
//    } catch (err) {
//      console.error("Telemetry API failed:", err);
//      setSpeedChartData([]);
//      setAvgSpeed("N/A");
//    } finally {
//      setTelemetryLoading(false);
//    }
//  }, [fromDate, toDate, selectedLocoNo]);
//
//  useEffect(() => {
//    fetchTelemetry();
//  }, [fetchTelemetry]);
//
//  // ── 3. Fetch packets (table + alert summary) ──────────────────────────────
//  const fetchPackets = useCallback(async () => {
//    if (!selectedLocoNo) return;
//    try {
//      setPacketsLoading(true);
//      const params = {
//        fromDate: toApiDateTime(fromDate),
//        toDate: toApiDateTime(toDate),
//        locoId: selectedLocoNo,
//      };
//      const res = await axios.get("/loco/packets", {
//        params,
//        headers: authHeader,
//      });
//      if (res.status === 200) {
//        const rawList = res.data.data ?? [];
//        setPacketsRawData(rawList);
//      }
//    } catch (err) {
//      console.error("Packets API failed:", err);
//      setPacketsRawData([]);
//    } finally {
//      setPacketsLoading(false);
//    }
//  }, [fromDate, toDate, selectedLocoNo]);
//
//  useEffect(() => {
//    fetchPackets();
//  }, [fetchPackets]);
//
//  // ── 4. Derive table rows + alert summary from packetsRawData + packetType ──
//  useEffect(() => {
//    if (!packetsRawData.length) {
//      setTableRows([]);
//      setFilteredRows([]);
//      setAlertSummaryData([]);
//      setWarningCount(0);
//      return;
//    }
//
//    const rows = [];
//
//    packetsRawData.forEach((topPacket) => {
//      const parentFields = {
//        _p_id: topPacket.id,
//        _p_locoId: topPacket.locoId,
//        _p_atDate: topPacket.atDate,
//        _p_pktType: topPacket.pktType,
//        _p_msgType: topPacket.msgType,
//        _p_msgLength: topPacket.msgLength,
//        _p_msgSequence: topPacket.msgSequence,
//        _p_kavachId: topPacket.kavachId,
//        _p_nmsSystemId: topPacket.nmsSystemId,
//        _p_systemVersionStr: topPacket.systemVersionStr,
//        _p_activeRadio: topPacket.activeRadio,
//        _p_radioStatus: topPacket.radioStatus,
//        _p_maSectionCount: topPacket.maSectionCount,
//        _p_crcHex: topPacket.crcHex,
//        _p_crcValid: topPacket.crcValid,
//        _p_firm: topPacket.firm,
//        _p_isParsed: topPacket.isParsed,
//        _p_createdAt: topPacket.createdAt,
//      };
//
//      const subPackets = topPacket[packetType] ?? [];
//
//      // CHANGED: Only add rows if subPackets exist and match selectedLocoNo
//      // Do NOT add parent-only rows when packet type is empty
//      if (subPackets.length > 0) {
//        subPackets.forEach((sub) => {
//          if (String(sub.sourceLocoId) === String(selectedLocoNo)) {
//            rows.push({ ...parentFields, ...sub });
//          }
//        });
//      }
//      // If subPackets is empty, we skip this topPacket entirely (no empty row shown)
//    });
//
//    setTableRows(rows);
//    setFilteredRows(rows);
//    setPage(0);
//
//    // CHANGED: Build alert summary using STATIC 4 alert types from older code
//    // Only count if emergencyStatusStr matches one of the 4 categories
//    const alertCounts = {
//      "SOS Alerts": 0,
//      "Emergency Brake": 0,
//      "Head-on Collision": 0,
//      "Normal Operations": 0,
//    };
//
//    packetsRawData.forEach((topPacket) => {
//      // Check both packet types for alerts
//      ["accessRequestPackets", "onboardRegularPackets"].forEach((type) => {
//        const subs = topPacket[type] ?? [];
//        subs.forEach((sub) => {
//          if (String(sub.sourceLocoId) === String(selectedLocoNo)) {
//            const status = sub.emergencyStatusStr ?? "Unknown";
//
//            // Check which static alert category this status belongs to
//            STATIC_ALERT_CONFIG.forEach((config) => {
//              if (config.match(status)) {
//                alertCounts[config.label]++;
//              }
//            });
//          }
//        });
//      });
//    });
//
//    // Build summary list with colors from static config
//    const summaryList = STATIC_ALERT_CONFIG.map((config) => ({
//      label: config.label,
//      count: alertCounts[config.label],
//      color: config.color,
//      bg: config.bg,
//    }));
//
//    setAlertSummaryData(summaryList);
//
//    // Warning Status = sum of all non-normal alerts (SOS + Brake + Collision)
//    // Or sum of all 4 categories based on your preference
//    // Based on older code, it seems to be total of all alerts shown
//    const totalWarnings = summaryList.reduce((acc, s) => acc + s.count, 0);
//    setWarningCount(totalWarnings);
//
//  }, [packetsRawData, packetType, selectedLocoNo]);
//
//  // ── 5. When packetType changes, reset visible columns to show all ────────
//  useEffect(() => {
//    const childCols =
//      packetType === "accessRequestPackets"
//        ? accessRequestColumns
//        : onboardRegularColumns;
//    setSelectedColumns([
//      ...parentColumns.map((c) => c.key),
//      ...childCols.map((c) => c.key),
//    ]);
//    setPage(0);
//  }, [packetType]);
//
//  // ── 6. Client-side search on tableRows ───────────────────────────────────
//  useEffect(() => {
//    if (!searchQuery) {
//      setFilteredRows(tableRows);
//      setPage(0);
//      return;
//    }
//    const low = searchQuery.toLowerCase();
//    setFilteredRows(
//      tableRows.filter((item) =>
//        Object.values(item).some(
//          (v) =>
//            v !== null &&
//            v !== undefined &&
//            v.toString().toLowerCase().includes(low),
//        ),
//      ),
//    );
//    setPage(0);
//  }, [searchQuery, tableRows]);
//
//  // ── 7. Clock tick ─────────────────────────────────────────────────────────
//  useEffect(() => {
//    const id = setInterval(
//      () => setCurrentDateTime(getFormattedDateTime()),
//      10000,
//    );
//    return () => clearInterval(id);
//  }, []);
//
//  // ── Derived ───────────────────────────────────────────────────────────────
//  const childColumns =
//    packetType === "accessRequestPackets"
//      ? accessRequestColumns
//      : onboardRegularColumns;
//
//  const allTableColumns = [...parentColumns, ...childColumns];
//
//  const visibleColumns = allTableColumns.filter((c) =>
//    selectedColumns.includes(c.key),
//  );
//
//  const selectedLoco = locomotives.find(
//    (l) => (l.nmsLocoId ?? l.locoNo) === selectedLocoNo,
//  );
//
//  const tooltipStyle = {
//    backgroundColor: isDark ? "#1e293b" : "#fff",
//    border: `1px solid ${isDark ? "#2d3a4a" : "#e2e6ea"}`,
//    borderRadius: 6,
//    fontSize: "0.8125rem",
//  };
//  const gridColor = isDark ? "rgba(255,255,255,0.06)" : "#f0f2f4";
//  const axisColor = isDark ? "rgba(255,255,255,0.3)" : "#9ca3b0";
//
//  const statCards = [
//    {
//      title: "Active Locomotives",
//      value: telemetryLoading ? "…" : String(activeLocoCount),
//      sub: "Distinct locos in range",
//      color: "#1565c0",
//      bg: "#e3f2fd",
//      icon: Train,
//    },
//    {
//      title: "Average Speed",
//      value: telemetryLoading ? "…" : avgSpeed !== null ? `${avgSpeed}` : "-",
//      sub: "km/h across selected range",
//      color: "#2e7d32",
//      bg: "#e8f5e9",
//      icon: Speed,
//    },
//    {
//      title: "Warning Status",
//      value: packetsLoading ? "…" : String(warningCount),
//      sub: "Non-normal emergency events",
//      color: "#e65100",
//      bg: "#fff3e0",
//      icon: Warning,
//    },
//  ];
//
//  // ── Handlers ──────────────────────────────────────────────────────────────
//  const handleChangePage = (_, p) => setPage(p);
//  const handleChangeRowsPerPage = (e) => {
//    setRowsPerPage(+e.target.value);
//    setPage(0);
//  };
//  const handleActionClick = (row) => {
//    setSelectedRow(row);
//    setDialogOpen(true);
//  };
//  const handleCloseDialog = () => {
//    setDialogOpen(false);
//    setSelectedRow(null);
//  };
//
//  const handleClearFilters = () => {
//    if (locomotives.length > 0) {
//      setSelectedLocoNo(locomotives[0].nmsLocoId ?? locomotives[0].locoNo);
//    }
//    setFromDate(buildDefaultFrom());
//    setToDate(buildDefaultTo());
//    setSearchQuery("");
//  };
//
//  const handleFromChange = (e) => {
//    if (e.target.value) setFromDate(new Date(e.target.value));
//  };
//  const handleToChange = (e) => {
//    if (e.target.value) setToDate(new Date(e.target.value));
//  };
//
//  // ── Cell value renderer ───────────────────────────────────────────────────
//  const formatDate = (val) => {
//    if (!val) return "-";
//    try {
//      const d = new Date(val);
//      const p = (n) => String(n).padStart(2, "0");
//      return `${p(d.getDate())}/${p(d.getMonth() + 1)}/${d.getFullYear()} ${p(d.getHours())}:${p(d.getMinutes())}`;
//    } catch {
//      return val;
//    }
//  };
//
//  const getCellValue = (row, key) => {
//    const val = row[key];
//
//    if (val === null || val === undefined || val === "") return "-";
//
//    if (key === "_p_atDate" || key === "_p_createdAt" || key === "createdAt") {
//      return formatDate(val);
//    }
//
//    if (key === "_p_crcValid" || key === "_p_isParsed") {
//      return val === true ? "✓ Yes" : val === false ? "✗ No" : String(val);
//    }
//
//    if (key === "emergencyStatusStr" && val) {
//      const colours = getEmergencyColor(val);
//      return colours ? (
//        <Box
//          component="span"
//          sx={{
//            display: "inline-block",
//            px: 1,
//            py: 0.25,
//            borderRadius: "8px",
//            fontSize: "0.75rem",
//            fontWeight: 700,
//            bgcolor: isDark ? alpha(colours.color, 0.15) : colours.bg,
//            color: colours.color,
//            whiteSpace: "nowrap",
//          }}
//        >
//          {val}
//        </Box>
//      ) : (
//        val
//      );
//    }
//
//    return String(val);
//  };
//
//  // ── Export to Excel ───────────────────────────────────────────────────────
//  const exportToExcel = () => {
//    setExcelLoading(true);
//    try {
//      const now = new Date();
//      const p = (v) => String(v).padStart(2, "0");
//      const ts = `${now.getFullYear()}-${p(now.getDate())}-${p(now.getMonth() + 1)} ${p(now.getHours())}:${p(now.getMinutes())}`;
//      const headers = visibleColumns.map((c) => c.label);
//      const rows = filteredRows.map((row) =>
//        visibleColumns.map((col) => {
//          const val = row[col.key];
//          if (val === null || val === undefined) return "-";
//          if (col.key === "createdAt")
//            return new Date(val).toLocaleString("en-GB");
//          return val;
//        }),
//      );
//      const dataToExport = [headers, ...rows];
//      const ws = XLSX.utils.aoa_to_sheet(dataToExport);
//      const cw = headers.map((_, ci) =>
//        Math.max(
//          ...dataToExport.map((r) => (r[ci] ? r[ci].toString().length : 0)),
//        ),
//      );
//      ws["!cols"] = cw.map((w) => ({ wch: Math.max(w + 2, 12) }));
//      const wb = XLSX.utils.book_new();
//      XLSX.utils.book_append_sheet(wb, ws, "Packets");
//      XLSX.writeFile(
//        wb,
//        `Loco_Packets_${selectedLoco?.locoNo ?? selectedLocoNo}_${ts}.xlsx`,
//      );
//    } catch (e) {
//      console.error(e);
//    } finally {
//      setExcelLoading(false);
//    }
//  };
//
//  const isFilterActive =
//    fromDate.getTime() !== buildDefaultFrom().getTime() ||
//    toDate.getTime() !== buildDefaultTo().getTime() ||
//    searchQuery;
//
//  // ── Render ────────────────────────────────────────────────────────────────
//  return (
//    <Container>
//      <Box sx={{ display: "grid", gap: 3 }}>
//        {/* ── Page header ── */}
//        <Box>
//          <Typography variant="h5" sx={{ fontWeight: 800, mt: 0.25 }}>
//            Loco Movement Live
//          </Typography>
//          <Typography variant="body2" color="text.secondary">
//            Real-time locomotive tracking and monitoring
//          </Typography>
//        </Box>
//
//        {/* ── Global Filters Card ── */}
//        <Card
//          sx={{
//            borderRadius: "20px",
//            border: `1px solid ${alpha(bgPrimary, 0.1)}`,
//            boxShadow: "0 4px 20px rgba(15,23,42,0.07)",
//          }}
//        >
//          <CardContent sx={{ p: { xs: 2, md: 2.5 } }}>
//            <Stack spacing={2}>
//              <Stack direction="row" alignItems="center">
//                <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
//                  Filters
//                </Typography>
//                {isFilterActive && (
//                  <Button
//                    size="small"
//                    onClick={handleClearFilters}
//                    sx={{
//                      ml: "auto",
//                      textTransform: "none",
//                      color: palette.error.main,
//                    }}
//                  >
//                    Clear All
//                  </Button>
//                )}
//              </Stack>
//
//              <Grid container spacing={2} alignItems="flex-start">
//                <Grid item xs={12} sm={6} md={3}>
//                  <FormControl fullWidth size="small">
//                    <InputLabel id="loco-label">Locomotive</InputLabel>
//                    <Select
//                      labelId="loco-label"
//                      value={selectedLocoNo ?? ""}
//                      label="Locomotive"
//                      disabled={locoLoading}
//                      onChange={(e) => setSelectedLocoNo(e.target.value)}
//                      startAdornment={
//                        locoLoading ? (
//                          <InputAdornment position="start">
//                            <CircularProgress size={16} />
//                          </InputAdornment>
//                        ) : null
//                      }
//                      sx={{
//                        borderRadius: "12px",
//                        bgcolor: palette.background.paper,
//                      }}
//                    >
//                      {locomotives.map((loco) => (
//                        <MenuItem
//                          key={loco.id}
//                          value={loco.nmsLocoId ?? loco.locoNo}
//                        >
//                          <Stack
//                            direction="row"
//                            alignItems="center"
//                            spacing={1}
//                          >
//                            <Train sx={{ fontSize: 16, color: bgPrimary }} />
//                            <Box>
//                              <Typography variant="body2" fontWeight={600}>
//                                {loco.locoNo}
//                              </Typography>
//                              <Typography
//                                variant="caption"
//                                color="text.secondary"
//                              >
//                                {loco.locoType?.name ?? "—"}
//                              </Typography>
//                            </Box>
//                          </Stack>
//                        </MenuItem>
//                      ))}
//                    </Select>
//                  </FormControl>
//                </Grid>
//                <LocalizationProvider dateAdapter={AdapterDayjs}>
//                <Grid item xs={12} sm={6} md={3}>
//                  <DateTimePicker
//                    label="From Date & Time"
//                    value={dayjs(fromDate)}
//                    onChange={(val) => setFromDate(val?.toDate())}
//                    format="DD/MM/YYYY hh:mm A"
//                    slotProps={{
//                      textField: {
//                        size: "small",
//                        fullWidth: true,
//                      },
//                    }}
//                  />
//                </Grid>
//                </LocalizationProvider>
//<LocalizationProvider dateAdapter={AdapterDayjs}>
//                <Grid item xs={12} sm={6} md={3}>
//                  <DateTimePicker
//                    label="To Date & Time"
//                    value={dayjs(toDate)}
//                    onChange={(val) => {
//                      const d = val?.toDate();
//                      if (d) {
//                        d.setSeconds(59);
//                        setToDate(d);
//                      }
//                    }}
//                    format="DD/MM/YYYY hh:mm A"
//                    slotProps={{
//                      textField: {
//                        size: "small",
//                        fullWidth: true,
//                      },
//                    }}
//                  />
//                </Grid>
//                </LocalizationProvider>
//              </Grid>
//
//              <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
//                {selectedLoco && (
//                  <Chip
//                    size="small"
//                    icon={<Train sx={{ fontSize: 14 }} />}
//                    label={`Loco: ${selectedLoco.locoNo} (${selectedLoco.locoType?.name ?? "—"})`}
//                    sx={{
//                      bgcolor: alpha(bgPrimary, 0.1),
//                      color: bgPrimary,
//                      fontWeight: 600,
//                    }}
//                  />
//                )}
//                <Chip
//                  size="small"
//                  label={`From: ${toDisplayLabel(fromDate)}`}
//                  onDelete={() => setFromDate(buildDefaultFrom())}
//                  sx={{
//                    bgcolor: alpha(bgSuccess, 0.1),
//                    color: bgSuccess,
//                    fontWeight: 600,
//                  }}
//                />
//                <Chip
//                  size="small"
//                  label={`To: ${toDisplayLabel(toDate)}`}
//                  onDelete={() => setToDate(buildDefaultTo())}
//                  sx={{
//                    bgcolor: alpha(bgSuccess, 0.1),
//                    color: bgSuccess,
//                    fontWeight: 600,
//                  }}
//                />
//              </Stack>
//            </Stack>
//          </CardContent>
//        </Card>
//
//        {/* ── Stat cards ── */}
//        <Box
//          sx={{
//            display: "grid",
//            gridTemplateColumns: { xs: "1fr", sm: "repeat(3, 1fr)" },
//            gap: 2,
//          }}
//        >
//          {statCards.map((card) => (
//            <Card
//              key={card.title}
//              sx={{
//                borderRadius: "20px",
//                border: `1px solid ${alpha(bgPrimary, 0.1)}`,
//                boxShadow: "0 4px 20px rgba(15,23,42,0.07)",
//                transition: "box-shadow 0.2s, transform 0.2s",
//                "&:hover": {
//                  boxShadow: "0 8px 32px rgba(15,23,42,0.12)",
//                  transform: "translateY(-2px)",
//                },
//              }}
//            >
//              <CardContent sx={{ p: 2.5 }}>
//                <Box
//                  sx={{
//                    display: "flex",
//                    alignItems: "flex-start",
//                    justifyContent: "space-between",
//                  }}
//                >
//                  <Box>
//                    <Typography
//                      variant="caption"
//                      sx={{
//                        fontWeight: 600,
//                        color: "text.secondary",
//                        display: "block",
//                        mb: 0.75,
//                        textTransform: "uppercase",
//                        letterSpacing: "0.08em",
//                      }}
//                    >
//                      {card.title}
//                    </Typography>
//                    <Typography
//                      sx={{
//                        fontSize: "1.75rem",
//                        fontWeight: 800,
//                        color: card.color,
//                        lineHeight: 1,
//                        mb: 0.5,
//                      }}
//                    >
//                      {card.value}
//                    </Typography>
//                    <Typography variant="caption" color="text.secondary">
//                      {card.sub}
//                    </Typography>
//                  </Box>
//                  <Box
//                    sx={{
//                      width: 48,
//                      height: 48,
//                      borderRadius: "16px",
//                      bgcolor: isDark ? alpha(card.color, 0.15) : card.bg,
//                      display: "flex",
//                      alignItems: "center",
//                      justifyContent: "center",
//                      flexShrink: 0,
//                    }}
//                  >
//                    <card.icon sx={{ fontSize: 24, color: card.color }} />
//                  </Box>
//                </Box>
//              </CardContent>
//            </Card>
//          ))}
//        </Box>
//
//        {/* ── Speed chart + Alert summary ── */}
//        <Box
//          sx={{
//            display: "grid",
//            gridTemplateColumns: { xs: "1fr", md: "2fr 1fr" },
//            gap: 2,
//          }}
//        >
//          <Card
//            sx={{
//              borderRadius: "20px",
//              border: `1px solid ${alpha(bgPrimary, 0.1)}`,
//              boxShadow: "0 4px 20px rgba(15,23,42,0.07)",
//            }}
//          >
//            <CardContent sx={{ p: 2.5 }}>
//              <Stack
//                direction="row"
//                alignItems="center"
//                justifyContent="space-between"
//                mb={0.5}
//              >
//                <Box>
//                  <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
//                    Speed Monitoring
//                  </Typography>
//                  <Typography variant="caption" color="text.secondary">
//                    {selectedLoco
//                      ? `Loco ${selectedLoco.locoNo} · ${toDisplayLabel(fromDate)} → ${toDisplayLabel(toDate)}`
//                      : `${toDisplayLabel(fromDate)} → ${toDisplayLabel(toDate)}`}
//                  </Typography>
//                </Box>
//                {telemetryLoading && <CircularProgress size={20} />}
//              </Stack>
//              <Box sx={{ mt: 1, height: 240 }}>
//                {!telemetryLoading && speedChartData.length === 0 ? (
//                  <Box
//                    sx={{
//                      height: "100%",
//                      display: "flex",
//                      alignItems: "center",
//                      justifyContent: "center",
//                    }}
//                  >
//                    <Typography variant="body2" color="text.secondary">
//                      No telemetry data for selected range
//                    </Typography>
//                  </Box>
//                ) : (
//                  <ResponsiveContainer width="100%" height="100%">
//                    <LineChart data={speedChartData}>
//                      <CartesianGrid strokeDasharray="3 3" stroke={gridColor} />
//                      <XAxis
//                        dataKey="time"
//                        stroke={axisColor}
//                        tick={{ fontSize: 10 }}
//                        tickLine={false}
//                        axisLine={{ stroke: gridColor }}
//                        interval="preserveStartEnd"
//                      />
//                      <YAxis
//                        stroke={axisColor}
//                        tick={{ fontSize: 11 }}
//                        tickLine={false}
//                        axisLine={{ stroke: gridColor }}
//                        domain={[0, "auto"]}
//                        label={{
//                          value: "km/h",
//                          angle: -90,
//                          position: "insideLeft",
//                          style: { fill: axisColor, fontSize: 11 },
//                        }}
//                      />
//                      <RechartsTooltip
//                        contentStyle={tooltipStyle}
//                        formatter={(v) => [`${v} km/h`, "Speed"]}
//                      />
//                      <Legend wrapperStyle={{ fontSize: 12 }} />
//                      <ReferenceLine
//                        y={110}
//                        stroke="#e65100"
//                        strokeDasharray="5 5"
//                        label={{
//                          value: "Speed Limit",
//                          fill: "#e65100",
//                          fontSize: 11,
//                        }}
//                      />
//                      <Line
//                        type="monotone"
//                        dataKey="speed"
//                        stroke={bgPrimary}
//                        strokeWidth={2.5}
//                        dot={{ fill: bgPrimary, r: 3 }}
//                        name="Speed (km/h)"
//                        isAnimationActive={false}
//                      />
//                    </LineChart>
//                  </ResponsiveContainer>
//                )}
//              </Box>
//            </CardContent>
//          </Card>
//
//          <Card
//            sx={{
//              borderRadius: "20px",
//              border: `1px solid ${alpha(bgPrimary, 0.1)}`,
//              boxShadow: "0 4px 20px rgba(15,23,42,0.07)",
//            }}
//          >
//            <CardContent sx={{ p: 2.5 }}>
//              <Stack
//                direction="row"
//                alignItems="center"
//                justifyContent="space-between"
//                mb={0.5}
//              >
//                <Box>
//                  <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
//                    Alert Summary
//                  </Typography>
//                  <Typography variant="caption" color="text.secondary">
//                    Emergency status counts
//                  </Typography>
//                </Box>
//                {packetsLoading && <CircularProgress size={20} />}
//              </Stack>
//              <Box
//                sx={{
//                  mt: 2,
//                  display: "flex",
//                  flexDirection: "column",
//                  gap: 1.2,
//                }}
//              >
//                {alertSummaryData.length === 0 && !packetsLoading ? (
//                  <Typography
//                    variant="body2"
//                    color="text.secondary"
//                    sx={{ textAlign: "center", mt: 2 }}
//                  >
//                    No data for selected range
//                  </Typography>
//                ) : (
//                  alertSummaryData.map((item) => (
//                    <Box
//                      key={item.label}
//                      sx={{
//                        display: "flex",
//                        alignItems: "center",
//                        justifyContent: "space-between",
//                        p: 1.5,
//                        borderRadius: "14px",
//                        bgcolor: isDark ? alpha(item.color, 0.1) : item.bg,
//                      }}
//                    >
//                      <Typography variant="body2" fontWeight={600}>
//                        {item.label}
//                      </Typography>
//                      <Typography
//                        variant="body2"
//                        fontWeight={800}
//                        sx={{ color: item.color }}
//                      >
//                        {item.count}
//                      </Typography>
//                    </Box>
//                  ))
//                )}
//              </Box>
//            </CardContent>
//          </Card>
//        </Box>
//
//        {/* ── Main table card ── */}
//        <Card
//          sx={{
//            borderRadius: "28px",
//            border: `1px solid ${alpha(bgPrimary, 0.12)}`,
//            boxShadow: "0 24px 56px rgba(15, 23, 42, 0.12)",
//          }}
//        >
//          <CardContent sx={{ p: { xs: 2, md: 3 } }}>
//            <Stack spacing={3}>
//              <Box
//                sx={{
//                  p: { xs: 2, md: 2.5 },
//                  borderRadius: "24px",
//                  border: `1px solid ${alpha(bgPrimary, 0.12)}`,
//                  background: `linear-gradient(135deg, ${alpha(bgPrimary, 0.08)} 0%, ${alpha(bgSuccess, 0.1)} 100%)`,
//                }}
//              >
//                <Stack
//                  direction={{ xs: "column", xl: "row" }}
//                  justifyContent="space-between"
//                  spacing={2}
//                >
//                  <Box sx={{ maxWidth: 620 }}>
//                    <Typography variant="h5" sx={{ fontWeight: 800, mt: 0.5 }}>
//                      NMS Loco Movement Live Data
//                    </Typography>
//                    <Stack
//                      direction="row"
//                      alignItems="center"
//                      spacing={0.6}
//                      sx={{ mt: 0.75 }}
//                    >
//                      <AccessTimeRoundedIcon
//                        sx={{ fontSize: 14, color: "text.secondary" }}
//                      />
//                      <Typography variant="caption" color="text.secondary">
//                        Last fetched at: <strong>{currentDateTime}</strong>
//                      </Typography>
//                    </Stack>
//                  </Box>
//                  <Stack
//                    direction={{ xs: "column", md: "row" }}
//                    spacing={1.2}
//                    useFlexGap
//                    flexWrap="wrap"
//                    alignItems={{ xs: "stretch", md: "center" }}
//                  >
//                    <FormControl size="small" sx={{ minWidth: 220 }}>
//                      <InputLabel id="packet-type-label">
//                        Packet Type
//                      </InputLabel>
//                      <Select
//                        labelId="packet-type-label"
//                        value={packetType}
//                        label="Packet Type"
//                        onChange={(e) => setPacketType(e.target.value)}
//                        sx={{
//                          borderRadius: "999px",
//                          bgcolor: alpha(palette.background.paper, 0.8),
//                        }}
//                      >
//                        {PACKET_TYPE_OPTIONS.map((opt) => (
//                          <MenuItem key={opt.value} value={opt.value}>
//                            {opt.label}
//                          </MenuItem>
//                        ))}
//                      </Select>
//                    </FormControl>
//
//                    <Button
//                      variant="outlined"
//                      startIcon={<ViewColumnRoundedIcon />}
//                      onClick={() => setOpenColFilter(true)}
//                      sx={{ borderRadius: "999px", textTransform: "none" }}
//                    >
//                      Columns
//                    </Button>
//                    <Button
//                      variant="outlined"
//                      onClick={exportToExcel}
//                      startIcon={
//                        excelLoading ? (
//                          <CircularProgress size={18} color="inherit" />
//                        ) : (
//                          <FileDownloadOutlinedIcon />
//                        )
//                      }
//                      sx={{ borderRadius: "999px", textTransform: "none" }}
//                    >
//                      Export
//                    </Button>
//                    <Button
//                      variant="outlined"
//                      onClick={fetchPackets}
//                      startIcon={<RefreshRoundedIcon />}
//                      sx={{ borderRadius: "999px", textTransform: "none" }}
//                    >
//                      Refresh
//                    </Button>
//                    <TextField
//                      size="small"
//                      placeholder="Search…"
//                      value={searchQuery}
//                      onChange={(e) => setSearchQuery(e.target.value)}
//                      sx={{
//                        minWidth: { xs: "100%", md: 220 },
//                        "& .MuiOutlinedInput-root": {
//                          borderRadius: "999px",
//                          bgcolor: alpha(palette.background.paper, 0.8),
//                        },
//                      }}
//                      InputProps={{
//                        startAdornment: (
//                          <InputAdornment position="start">
//                            <SearchRoundedIcon color="action" />
//                          </InputAdornment>
//                        ),
//                      }}
//                    />
//                  </Stack>
//                </Stack>
//              </Box>
//
//              <TableContainer
//                component={Paper}
//                elevation={0}
//                sx={{
//                  position: "relative",
//                  mx: 0,
//                  width: "100%",
//                  overflow: "auto",
//                  borderRadius: "24px",
//                  border: `1px solid ${alpha(bgPrimary, 0.12)}`,
//                  minHeight: "50vh",
//                  maxHeight: 640,
//                  scrollbarGutter: "stable",
//                  "&::-webkit-scrollbar": { height: 12, width: 10 },
//                  "&::-webkit-scrollbar-track": {
//                    background: alpha(bgPrimary, 0.08),
//                    borderRadius: "999px",
//                  },
//                  "&::-webkit-scrollbar-thumb": {
//                    background: alpha(bgPrimary, 0.32),
//                    borderRadius: "999px",
//                  },
//                }}
//              >
//                {(packetsLoading || filteredRows.length === 0) && (
//                  <Box
//                    sx={{
//                      position: "absolute",
//                      inset: 0,
//                      display: "flex",
//                      alignItems: "center",
//                      justifyContent: "center",
//                    }}
//                  >
//                    <Stack spacing={2} alignItems="center">
//                      {packetsLoading ? (
//                        <>
//                          <CircularProgress />
//                          <Typography color="text.secondary">
//                            Loading packets…
//                          </Typography>
//                        </>
//                      ) : (
//                        <>
//                          <Box
//                            sx={{
//                              width: 56,
//                              height: 56,
//                              borderRadius: "20px",
//                              display: "grid",
//                              placeItems: "center",
//                              bgcolor: alpha(bgPrimary, 0.08),
//                              color: bgPrimary,
//                            }}
//                          >
//                            <TravelExploreRoundedIcon />
//                          </Box>
//                          <Typography variant="h6" sx={{ fontWeight: 700 }}>
//                            No Data Available
//                          </Typography>
//                          <Typography variant="body2" color="text.secondary">
//                            No{" "}
//                            {
//                              PACKET_TYPE_OPTIONS.find(
//                                (o) => o.value === packetType,
//                              )?.label
//                            }{" "}
//                            found for selected filters
//                          </Typography>
//                        </>
//                      )}
//                    </Stack>
//                  </Box>
//                )}
//
//                <StyledTable>
//                  <TableHead>
//                    <TableRow>
//                      <TableCell
//                        align="center"
//                        sx={{
//                          minWidth: 72,
//                          width: 72,
//                          position: "sticky",
//                          left: 0,
//                          zIndex: 3,
//                          bgcolor: "background.paper",
//                          backgroundClip: "padding-box",
//                        }}
//                      >
//                        No.
//                      </TableCell>
//                      {visibleColumns.map((col) => (
//                        <TableCell
//                          key={col.key}
//                          align="center"
//                          sx={{
//                            minWidth: colWidthMapPackets[col.key] ?? 120,
//                            width: colWidthMapPackets[col.key] ?? 120,
//                          }}
//                        >
//                          {col.label}
//                        </TableCell>
//                      ))}
//                      <TableCell
//                        align="center"
//                        sx={{
//                          minWidth: 80,
//                          width: 80,
//                          position: "sticky",
//                          right: 0,
//                          zIndex: 3,
//                          bgcolor: "background.paper",
//                          backgroundClip: "padding-box",
//                        }}
//                      >
//                        Action
//                      </TableCell>
//                    </TableRow>
//                  </TableHead>
//
//                  <TableBody>
//                    {filteredRows
//                      .slice(
//                        page * rowsPerPage,
//                        page * rowsPerPage + rowsPerPage,
//                      )
//                      .map((row, index) => (
//                        <TableRow
//                          key={index}
//                          sx={{
//                            "&:hover td": {
//                              backgroundColor: alpha(bgPrimary, 0.04),
//                            },
//                          }}
//                        >
//                          <TableCell
//                            align="center"
//                            sx={{
//                              position: "sticky",
//                              left: 0,
//                              zIndex: 2,
//                              bgcolor: "background.paper",
//                              backgroundClip: "padding-box",
//                            }}
//                          >
//                            <Box
//                              sx={{
//                                width: 34,
//                                height: 34,
//                                mx: "auto",
//                                borderRadius: "50%",
//                                display: "grid",
//                                placeItems: "center",
//                                fontWeight: 700,
//                                color: bgPrimary,
//                                bgcolor: alpha(bgPrimary, 0.08),
//                              }}
//                            >
//                              {index + 1 + page * rowsPerPage}
//                            </Box>
//                          </TableCell>
//                          {visibleColumns.map((col) => (
//                            <TableCell key={col.key} align="center">
//                              {getCellValue(row, col.key)}
//                            </TableCell>
//                          ))}
//                          <TableCell
//                            align="center"
//                            sx={{
//                              position: "sticky",
//                              right: 0,
//                              zIndex: 2,
//                              bgcolor: "background.paper",
//                              backgroundClip: "padding-box",
//                              boxShadow: "-4px 0 12px rgba(0,0,0,0.06)",
//                            }}
//                          >
//                            <IconButton
//                              onClick={() => handleActionClick(row)}
//                              size="small"
//                              sx={{
//                                bgcolor: alpha(bgPrimary, 0.08),
//                                color: bgPrimary,
//                                "&:hover": { bgcolor: alpha(bgPrimary, 0.16) },
//                              }}
//                            >
//                              <Tooltip title="View Details">
//                                <RemoveRedEyeIcon fontSize="small" />
//                              </Tooltip>
//                            </IconButton>
//                          </TableCell>
//                        </TableRow>
//                      ))}
//                  </TableBody>
//                </StyledTable>
//              </TableContainer>
//
//              <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
//                <CustomTablePagination
//                  rowsPerPageOptions={[7, 25, 50, 100]}
//                  colSpan={3}
//                  count={filteredRows.length}
//                  rowsPerPage={rowsPerPage}
//                  page={page}
//                  slotProps={{
//                    select: { "aria-label": "Rows Per Page" },
//                    actions: {
//                      showFirstButton: true,
//                      showLastButton: true,
//                      slots: {
//                        firstPageIcon: FirstPageRoundedIcon,
//                        lastPageIcon: LastPageRoundedIcon,
//                        nextPageIcon: ChevronRightRoundedIcon,
//                        backPageIcon: ChevronLeftRoundedIcon,
//                      },
//                    },
//                  }}
//                  onPageChange={handleChangePage}
//                  onRowsPerPageChange={handleChangeRowsPerPage}
//                />
//              </Box>
//            </Stack>
//          </CardContent>
//        </Card>
//      </Box>
//
//      {/* ── Column Filter Dialog ── */}
//      <Dialog
//        open={openColFilter}
//        onClose={() => setOpenColFilter(false)}
//        maxWidth="sm"
//        fullWidth
//        PaperProps={{ sx: { borderRadius: "28px", overflow: "hidden" } }}
//      >
//        <Box
//          sx={{
//            p: 3,
//            color: "#fff",
//            background: `linear-gradient(135deg, ${bgPrimary} 0%, ${bgSuccess} 160%)`,
//          }}
//        >
//          <Stack direction="row" justifyContent="space-between" spacing={2}>
//            <Typography variant="h5" sx={{ fontWeight: 800 }}>
//              Customize Visible Columns
//            </Typography>
//            <IconButton
//              onClick={() => setOpenColFilter(false)}
//              sx={{
//                width: 40,
//                height: 40,
//                color: "#fff",
//                bgcolor: "rgba(255,255,255,0.12)",
//              }}
//            >
//              <CloseIcon />
//            </IconButton>
//          </Stack>
//        </Box>
//        <DialogContent sx={{ p: 3 }}>
//          <Typography
//            variant="overline"
//            sx={{
//              fontWeight: 700,
//              color: bgPrimary,
//              letterSpacing: "0.1em",
//              display: "block",
//              mb: 1,
//            }}
//          >
//            Packet Fields (Fixed)
//          </Typography>
//          <Grid container spacing={1.5} sx={{ mb: 2 }}>
//            {parentColumns.map((col) => {
//              const checked = selectedColumns.includes(col.key);
//              return (
//                <Grid item xs={12} sm={6} key={col.key}>
//                  <Box
//                    sx={{
//                      p: 1.5,
//                      borderRadius: "20px",
//                      border: `1px solid ${checked ? alpha(bgPrimary, 0.24) : alpha(palette.divider, 0.9)}`,
//                      bgcolor: checked
//                        ? alpha(bgPrimary, 0.05)
//                        : palette.background.paper,
//                      transition: "all 150ms ease",
//                    }}
//                  >
//                    <FormControlLabel
//                      sx={{ m: 0, width: "100%" }}
//                      control={
//                        <Checkbox
//                          checked={checked}
//                          onChange={(e) =>
//                            setSelectedColumns((prev) =>
//                              e.target.checked
//                                ? [...prev, col.key]
//                                : prev.filter((k) => k !== col.key),
//                            )
//                          }
//                        />
//                      }
//                      label={
//                        <Box>
//                          <Typography
//                            sx={{ fontWeight: 700, fontSize: "0.875rem" }}
//                          >
//                            {col.label}
//                          </Typography>
//                        </Box>
//                      }
//                    />
//                  </Box>
//                </Grid>
//              );
//            })}
//          </Grid>
//          <Typography
//            variant="overline"
//            sx={{
//              fontWeight: 700,
//              color: bgSuccess,
//              letterSpacing: "0.1em",
//              display: "block",
//              mb: 1,
//            }}
//          >
//            {packetType === "accessRequestPackets"
//              ? "Access Request Packet Fields"
//              : "Onboard Regular Packet Fields"}
//          </Typography>
//          <Grid container spacing={1.5}>
//            {childColumns.map((col) => {
//              const checked = selectedColumns.includes(col.key);
//              return (
//                <Grid item xs={12} sm={6} key={col.key}>
//                  <Box
//                    sx={{
//                      p: 1.5,
//                      borderRadius: "20px",
//                      border: `1px solid ${checked ? alpha(bgSuccess, 0.24) : alpha(palette.divider, 0.9)}`,
//                      bgcolor: checked
//                        ? alpha(bgSuccess, 0.05)
//                        : palette.background.paper,
//                      transition: "all 150ms ease",
//                    }}
//                  >
//                    <FormControlLabel
//                      sx={{ m: 0, width: "100%" }}
//                      control={
//                        <Checkbox
//                          checked={checked}
//                          onChange={(e) =>
//                            setSelectedColumns((prev) =>
//                              e.target.checked
//                                ? [...prev, col.key]
//                                : prev.filter((k) => k !== col.key),
//                            )
//                          }
//                        />
//                      }
//                      label={
//                        <Box>
//                          <Typography
//                            sx={{ fontWeight: 700, fontSize: "0.875rem" }}
//                          >
//                            {col.label}
//                          </Typography>
//                        </Box>
//                      }
//                    />
//                  </Box>
//                </Grid>
//              );
//            })}
//          </Grid>
//        </DialogContent>
//        <DialogActions sx={{ px: 3, pb: 3, pt: 0 }}>
//          <Button
//            onClick={() =>
//              setSelectedColumns([
//                ...parentColumns.map((c) => c.key),
//                ...childColumns.map((c) => c.key),
//              ])
//            }
//            sx={{ textTransform: "none" }}
//          >
//            Reset All
//          </Button>
//          <Button
//            variant="contained"
//            onClick={() => setOpenColFilter(false)}
//            sx={{ textTransform: "none", borderRadius: "999px", px: 2.5 }}
//          >
//            Done
//          </Button>
//        </DialogActions>
//      </Dialog>
//
//      {/* ── Row Detail Dialog ── */}
//      <Dialog
//        open={dialogOpen}
//        onClose={handleCloseDialog}
//        fullWidth
//        maxWidth="md"
//        PaperProps={{ sx: { borderRadius: "28px", overflow: "hidden" } }}
//      >
//        <Box
//          sx={{
//            p: 3,
//            color: "#fff",
//            background: `linear-gradient(135deg, ${bgPrimary} 0%, ${bgSuccess} 160%)`,
//          }}
//        >
//          <Stack direction="row" justifyContent="space-between" spacing={2}>
//            <Box>
//              <Typography variant="h5" sx={{ fontWeight: 800 }}>
//                Packet Details
//              </Typography>
//              {selectedRow && (
//                <Typography sx={{ mt: 0.5, color: "rgba(255,255,255,0.8)" }}>
//                  Loco ID: {selectedRow.sourceLocoId} · {selectedRow.pktTypeStr}
//                </Typography>
//              )}
//            </Box>
//            <IconButton
//              onClick={handleCloseDialog}
//              sx={{
//                width: 40,
//                height: 40,
//                color: "#fff",
//                bgcolor: "rgba(255,255,255,0.12)",
//              }}
//            >
//              <CloseIcon />
//            </IconButton>
//          </Stack>
//        </Box>
//        <DialogContent sx={{ p: 3 }}>
//          {selectedRow && (
//            <Stack spacing={2}>
//              {[
//                {
//                  title: "Packet Info",
//                  fields: [
//                    ["ID", selectedRow._p_id],
//                    ["Loco ID", selectedRow._p_locoId],
//                    [
//                      "At Date",
//                      selectedRow._p_atDate
//                        ? formatDate(selectedRow._p_atDate)
//                        : "-",
//                    ],
//                    ["Pkt Type", selectedRow._p_pktType],
//                    ["Msg Type", selectedRow._p_msgType],
//                    ["Msg Length", selectedRow._p_msgLength],
//                    ["Msg Sequence", selectedRow._p_msgSequence],
//                    ["Kavach ID", selectedRow._p_kavachId],
//                    ["NMS System ID", selectedRow._p_nmsSystemId],
//                    ["System Version", selectedRow._p_systemVersionStr],
//                    ["Active Radio", selectedRow._p_activeRadio],
//                    ["Radio Status", selectedRow._p_radioStatus],
//                    ["MA Section Count", selectedRow._p_maSectionCount],
//                    ["CRC Hex", selectedRow._p_crcHex],
//                    [
//                      "CRC Valid",
//                      selectedRow._p_crcValid === true
//                        ? "✓ Yes"
//                        : selectedRow._p_crcValid === false
//                          ? "✗ No"
//                          : "-",
//                    ],
//                    ["Firm", selectedRow._p_firm],
//                    [
//                      "Is Parsed",
//                      selectedRow._p_isParsed === true
//                        ? "✓ Yes"
//                        : selectedRow._p_isParsed === false
//                          ? "✗ No"
//                          : "-",
//                    ],
//                    [
//                      "Created At",
//                      selectedRow._p_createdAt
//                        ? formatDate(selectedRow._p_createdAt)
//                        : "-",
//                    ],
//                  ],
//                },
//                {
//                  title: "Sub-Packet Details",
//                  fields: [
//                    ["Pkt Type Str", selectedRow.pktTypeStr],
//                    ["Pkt Length", selectedRow.pktLength],
//                    ["Frame Num", selectedRow.frameNum],
//                    ["Frame Time", selectedRow.frameTime],
//                    ["Source Loco ID", selectedRow.sourceLocoId],
//                    ["Loco Version", selectedRow.sourceLocoVersionStr],
//                    ["Abs Location", selectedRow.absLocoLoc],
//                    [
//                      "Train Length",
//                      selectedRow.trainLength
//                        ? `${selectedRow.trainLength} m`
//                        : "-",
//                    ],
//                    [
//                      "Train Speed",
//                      selectedRow.trainSpeed
//                        ? `${selectedRow.trainSpeed} km/h`
//                        : "-",
//                    ],
//                    ["Direction", selectedRow.movementDirStr],
//                    ["Emergency Status", selectedRow.emergencyStatusStr],
//                    ["Loco Mode", selectedRow.locoModeStr],
//                    ["Approaching Stn", selectedRow.approachingStnId],
//                    ["Last RFID Tag", selectedRow.lastRfidTag],
//                    ["TIN", selectedRow.tin],
//                    ["Latitude", selectedRow.latitudeDeg],
//                    ["Longitude", selectedRow.longitudeDeg],
//                    ["Loco Rnd Num", selectedRow.locoRndNum],
//                    ["Packet CRC", selectedRow.pktCrc],
//                  ],
//                },
//              ].map((section) => (
//                <Box key={section.title}>
//                  <Typography
//                    variant="overline"
//                    sx={{
//                      color: bgPrimary,
//                      fontWeight: 700,
//                      letterSpacing: "0.1em",
//                    }}
//                  >
//                    {section.title}
//                  </Typography>
//                  <Paper
//                    elevation={0}
//                    sx={{
//                      mt: 1,
//                      p: 2,
//                      borderRadius: "20px",
//                      border: `1px solid ${alpha(bgPrimary, 0.12)}`,
//                      bgcolor: alpha(bgPrimary, 0.03),
//                    }}
//                  >
//                    <Grid container spacing={1.5}>
//                      {section.fields.map(([label, value]) => (
//                        <Grid item xs={12} sm={6} md={4} key={label}>
//                          <Typography
//                            variant="caption"
//                            color="text.secondary"
//                            sx={{
//                              fontWeight: 600,
//                              textTransform: "uppercase",
//                              letterSpacing: "0.06em",
//                            }}
//                          >
//                            {label}
//                          </Typography>
//                          <Typography
//                            sx={{ fontWeight: 700, fontSize: "0.92rem" }}
//                          >
//                            {value !== null &&
//                            value !== undefined &&
//                            value !== ""
//                              ? String(value)
//                              : "-"}
//                          </Typography>
//                        </Grid>
//                      ))}
//                    </Grid>
//                  </Paper>
//                </Box>
//              ))}
//            </Stack>
//          )}
//        </DialogContent>
//        <DialogActions sx={{ px: 3, pb: 3, pt: 0 }}>
//          <Button
//            variant="outlined"
//            color="secondary"
//            onClick={handleCloseDialog}
//            sx={{ textTransform: "none", borderRadius: "999px", px: 2.5 }}
//          >
//            Close
//          </Button>
//        </DialogActions>
//      </Dialog>
//    </Container>
//  );
//};
//
//export default LocoMovementLive;