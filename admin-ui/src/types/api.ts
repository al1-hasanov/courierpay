export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

export type AuthResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
};

export type Company = {
  id: number;
  name: string;
  commissionRate: number;
};

export type Courier = {
  id: number;
  userId: number;
  fullName: string;
  companyId: number;
  companyName: string;
  phoneNumber?: string;
  active: boolean;
};

export type EarningStatus = 'PENDING' | 'PROCESSED';
export type PayoutStatus = 'REQUESTED' | 'COMPLETED' | 'REJECTED';

export type Earning = {
  id: number;
  courierId: number;
  grossAmount: number;
  commissionAmount: number;
  netAmount: number;
  status: EarningStatus;
  workDate: string;
};

export type Payout = {
  id: number;
  courierId: number;
  amount: number;
  status: PayoutStatus;
};

export type Balance = {
  courierId: number;
  availableAmount: number;
  reservedAmount: number;
};


export type AuditLog = {
  id: number;
  actorUserId?: number;
  actorEmail?: string;
  actorRole?: string;
  action: string;
  entityType?: string;
  entityId?: string;
  status: 'SUCCESS' | 'FAILURE';
  message?: string;
  metadataJson?: string;
  ipAddress?: string;
  userAgent?: string;
  createdAt: string;
};
